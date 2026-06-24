package com.qrpro.application.service;
import com.qrpro.domain.model.ApiKey;
import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.ApiKeyRepositoryPort;
import com.qrpro.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepositoryPort apiKeyRepository;
    private final UserRepositoryPort userRepository;
    private static final String KEY_PREFIX = "qrpro_live_";

    @Transactional
    public ApiKeyGenerationResult generateKey(UUID userId, String name) {
        String plainKey = KEY_PREFIX + UUID.randomUUID().toString().replace("-", "");
        String keyHash = hashKey(plainKey);
        ApiKey apiKey = new ApiKey(UUID.randomUUID(), userId, keyHash, name, null, OffsetDateTime.now(), true);
        apiKeyRepository.save(apiKey);
        log.info("API Key generated for user {}", userId);
        return new ApiKeyGenerationResult(apiKey.id(), plainKey, apiKey.name(), apiKey.createdAt());
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String apiKey) {
        if (apiKey == null || !apiKey.startsWith(KEY_PREFIX)) return Optional.empty();
        String keyHash = hashKey(apiKey);
        return apiKeyRepository.findByKeyHash(keyHash)
            .filter(ApiKey::active)
            .map(key -> { apiKeyRepository.save(key.markUsed()); return userRepository.findById(key.userId()); })
            .orElse(Optional.empty());
    }

    @Transactional(readOnly = true)
    public List<ApiKey> listByUser(UUID userId) { return apiKeyRepository.findByUserId(userId); }

    @Transactional
    public void revokeKey(UUID keyId, UUID userId) {
        apiKeyRepository.findByUserId(userId).stream().filter(k -> k.id().equals(keyId)).findFirst()
            .ifPresent(k -> apiKeyRepository.save(k.deactivate()));
    }

    private String hashKey(String plainKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainKey.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException("SHA-256 not available", e); }
    }

    public record ApiKeyGenerationResult(UUID id, String plainKey, String name, OffsetDateTime createdAt) {}
}
