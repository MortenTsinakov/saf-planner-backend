package hr.adriaticanimation.saf_planner.services.authentication;

import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.entities.user.UserRole;
import hr.adriaticanimation.saf_planner.utils.JwtKeyProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @InjectMocks
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setup() {
        String jwtSecret = "asdfalsdfaslkdjfbaskdfbasdlkfjabdslfkjsabdlkasbdflaksjdbfalksdjfbaksdjbfaksdjfbaksldb";
        when(jwtKeyProvider.getSecretKey()).thenReturn(jwtSecret.getBytes());
        long expirationTime = 900000;
        ReflectionTestUtils.setField(jwtService, "expirationTime", expirationTime);

        user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);
        user.setFirstName("John");
        user.setLastName("Smith");
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(user);
        String expected = user.getEmail();

        String actual = jwtService.extractUsername(token);

        assertEquals(expected, actual);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void testIsTokenValidWrongEmail() {
        String token = jwtService.generateToken(user);
        User differentUser = new User();
        differentUser.setEmail("wrongemail@email.com");
        assertFalse(jwtService.isTokenValid(token, differentUser));
    }

    @Test
    void testIsTokenValidTokenHasExpired() {
        ReflectionTestUtils.setField(jwtService, "expirationTime", 0);

        String token = jwtService.generateToken(user);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }
}