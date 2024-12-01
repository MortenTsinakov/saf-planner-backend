package hr.adriaticanimation.saf_planner.services.authentication;

import hr.adriaticanimation.saf_planner.entities.authentication.RefreshToken;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.RefreshTokenException;
import hr.adriaticanimation.saf_planner.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for refresh token operations.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${security.refresh-token.expiration-time}")
    private long expirationTime;
    @Value("${security.refresh-token.renewal}")
    private long renewalTime;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Timestamp.from(Instant.now().plusMillis(expirationTime)))
                .user(user)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteUsersPreviousRefreshToken(User user) {
        Optional<RefreshToken> token = refreshTokenRepository.findByUser(user);
        token.ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken prolongExpiryDate(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Timestamp.from(Instant.now().minusMillis(renewalTime))) > 0) {
            refreshToken.setExpiryDate(Timestamp.from(Instant.now().minusMillis(expirationTime)));
            refreshToken = refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyExpirationDate(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Timestamp.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token has expired");
        }
        refreshToken = prolongExpiryDate(refreshToken);
        return refreshToken;
    }
}
