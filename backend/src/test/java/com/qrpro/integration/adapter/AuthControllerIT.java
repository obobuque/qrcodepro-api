package com.qrpro.integration.adapter;
import com.qrpro.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIT extends AbstractIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM scan_events");
        jdbcTemplate.execute("DELETE FROM qr_codes");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test void register_should_return_201_with_token() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.token").exists());
    }

    @Test void register_should_return_409_when_email_already_exists() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"user1\",\"email\":\"dup@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"user2\",\"email\":\"dup@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isConflict());
    }

    @Test void login_should_return_200_with_token_after_register() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"logintest\",\"email\":\"login@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"logintest\",\"password\":\"password123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
    }

    @Test void login_should_return_409_for_invalid_credentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"nonexistent\",\"password\":\"wrongpassword\"}"))
            .andExpect(status().isConflict());
    }
}
