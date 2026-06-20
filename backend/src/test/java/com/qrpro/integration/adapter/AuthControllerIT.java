package com.qrpro.integration.adapter;

import com.qrpro.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpEntity<String> jsonRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String uniqueEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }

    private String uniqueUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void register_should_return_201_with_token() {
        String email = uniqueEmail();
        String username = uniqueUsername();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/register",
                jsonRequest("""
                        {"username":"%s","email":"%s","password":"senha123"}
                        """.formatted(username, email)),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("token");
        assertThat((String) response.getBody().get("token")).isNotBlank();
    }

    @Test
    void register_should_return_409_for_duplicate_email() {
        String email = uniqueEmail();
        String body = """
                {"username":"%s","email":"%s","password":"senha123"}
                """.formatted(uniqueUsername(), email);

        restTemplate.postForEntity("/api/v1/auth/register", jsonRequest(body), Map.class);

        String body2 = """
                {"username":"%s","email":"%s","password":"senha123"}
                """.formatted(uniqueUsername(), email);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/register", jsonRequest(body2), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_should_return_200_with_token_after_register() {
        String email = uniqueEmail();
        String username = uniqueUsername();

        restTemplate.postForEntity("/api/v1/auth/register",
                jsonRequest("""
                        {"username":"%s","email":"%s","password":"senha123"}
                        """.formatted(username, email)), Map.class);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login",
                jsonRequest("""
                        {"username":"%s","password":"senha123"}
                        """.formatted(email)),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
    }

    @Test
    void login_should_return_409_for_wrong_password() {
        String email = uniqueEmail();
        String username = uniqueUsername();

        restTemplate.postForEntity("/api/v1/auth/register",
                jsonRequest("""
                        {"username":"%s","email":"%s","password":"correta"}
                        """.formatted(username, email)), Map.class);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login",
                jsonRequest("""
                        {"username":"%s","password":"errada"}
                        """.formatted(email)),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
