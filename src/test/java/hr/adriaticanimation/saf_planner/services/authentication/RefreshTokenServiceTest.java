package hr.adriaticanimation.saf_planner.services.authentication;

import hr.adriaticanimation.saf_planner.entities.authentication.RefreshToken;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.RefreshTokenException;
import hr.adriaticanimation.saf_planner.repositories.authentication.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final long renewalTime = 10000;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(refreshTokenService, "expirationTime", 60000);
        ReflectionTestUtils.setField(refreshTokenService, "renewalTime", renewalTime);
    }

    @Test
    void createRefreshToken() {
        User user = new User();
        RefreshToken refreshToken = new RefreshToken();

        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);
        refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).save(any());
    }

    @Test
    void deleteUsersPreviousRefreshTokenFound() {
        User user = new User();
        RefreshToken refreshToken = new RefreshToken();

        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));
        refreshTokenService.deleteUsersPreviousRefreshToken(user);

        verify(refreshTokenRepository).findByUser(user);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteUsersPreviousRefreshTokenNotFound() {
        User user = new User();

        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        refreshTokenService.deleteUsersPreviousRefreshToken(user);

        verify(refreshTokenRepository).findByUser(user);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void findByToken() {
        String token = "token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(new RefreshToken()));
        refreshTokenService.findByToken(token);
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void prolongExpiryDateShouldBeProlonged() {
        Timestamp initialTimestamp = Timestamp.from(Instant
                .now()
                .plusMillis(renewalTime)
        );
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(initialTimestamp);

        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        RefreshToken output = refreshTokenService.prolongExpiryDate(refreshToken);

        assert output.getExpiryDate().compareTo(initialTimestamp) > 0;
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void prolongExpiryDateShouldNotBeProlonged() {
        Timestamp initialTimestamp = Timestamp.from(Instant
                .now()
                .plusMillis(renewalTime * 2)
        );
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(initialTimestamp);

        RefreshToken output = refreshTokenService.prolongExpiryDate(refreshToken);

        verifyNoInteractions(refreshTokenRepository);
        assertEquals(initialTimestamp, output.getExpiryDate());
    }

    @Test
    void verifyExpirationDateValid() {
        Timestamp initialTimestamp = Timestamp.from(Instant.now().plusMillis(renewalTime));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(initialTimestamp);

        assertDoesNotThrow(() -> refreshTokenService.verifyExpirationDate(refreshToken));
    }

    @Test
    void verifyExpirationDateInvalid() {
        Timestamp initialTimestamp = Timestamp.from(Instant.now().minusMillis(renewalTime));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(initialTimestamp);

        assertThrows(RefreshTokenException.class, () -> refreshTokenService.verifyExpirationDate(refreshToken));

        verify(refreshTokenRepository).delete(refreshToken);
    }
}