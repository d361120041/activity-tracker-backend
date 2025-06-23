package activity_tracker_backend.service;

import activity_tracker_backend.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtils jwtUtils;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public RefreshTokenService(RedisTemplate redisTemplate, JwtUtils jwtUtils) {
        this.redisTemplate = redisTemplate;
        this.jwtUtils = jwtUtils;
    }

    public void saveRefreshToken(String userEmail, String refreshToken) {
        redisTemplate.opsForValue().set(refreshToken, userEmail, refreshTokenExpirationMs, TimeUnit.MILLISECONDS);
    }

    public String getUserEmailByRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    public boolean isValidRefreshToken(String userEmail, String refreshToken) {
        String storedUserEmail = getUserEmailByRefreshToken(refreshToken);
        return storedUserEmail!=null && storedUserEmail.equals(userEmail) && jwtUtils.validateRefreshToken(refreshToken);
    }

    public void revokeRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
