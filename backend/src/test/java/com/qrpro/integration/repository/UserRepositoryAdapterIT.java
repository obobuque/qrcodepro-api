package com.qrpro.integration.repository;

import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.UserRepositoryPort;
import com.qrpro.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
class UserRepositoryAdapterIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    private User newUser() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return new User(UUID.randomUUID(), "user_" + id,
                "user_" + id + "@email.com", "$2a$bcrypt", true, null, null);
    }

    @Test
    void save_and_findByEmail_should_persist_and_retrieve_user() {
        User user = newUser();
        User saved = userRepositoryPort.save(user);

        Optional<User> found = userRepositoryPort.findByEmail(user.email());

        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo(user.email());
        assertThat(found.get().username()).isEqualTo(user.username());
        assertThat(found.get().id()).isEqualTo(saved.id());
    }

    @Test
    void findByEmail_should_return_empty_for_unknown_email() {
        Optional<User> found = userRepositoryPort.findByEmail("naoexiste_" + UUID.randomUUID() + "@email.com");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_should_return_true_when_user_exists() {
        User user = newUser();
        userRepositoryPort.save(user);
        assertThat(userRepositoryPort.existsByEmail(user.email())).isTrue();
    }

    @Test
    void existsByEmail_should_return_false_when_user_not_exists() {
        assertThat(userRepositoryPort.existsByEmail("fantasma_" + UUID.randomUUID() + "@email.com")).isFalse();
    }

    @Test
    void existsByUsername_should_return_true_when_username_taken() {
        User user = newUser();
        userRepositoryPort.save(user);
        assertThat(userRepositoryPort.existsByUsername(user.username())).isTrue();
    }

    @Test
    void findByUsername_should_return_user_when_exists() {
        User user = newUser();
        userRepositoryPort.save(user);

        Optional<User> found = userRepositoryPort.findByUsername(user.username());
        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo(user.email());
    }
}
