package hr.adriaticanimation.saf_planner.services.authentication;

import hr.adriaticanimation.saf_planner.dtos.authentication.SignInRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.SignOutResponse;
import hr.adriaticanimation.saf_planner.dtos.authentication.SignUpRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.UserAuthenticationResponse;
import hr.adriaticanimation.saf_planner.entities.authentication.RefreshToken;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.RefreshTokenException;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.SignUpException;
import hr.adriaticanimation.saf_planner.mappers.authentication.UserMapper;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testSignUp() {
        Long id = 1L;
        String email = "email@email.com";
        String firstName = "John";
        String lastName = "Doe";
        String password = "password";
        SignUpRequest signUpRequest = new SignUpRequest(
                email,
                firstName,
                lastName,
                password
        );

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);

        RefreshToken refreshToken = new RefreshToken();
        Timestamp expirationTime = Timestamp.valueOf("2025-01-01 00:00:00");
        refreshToken.setId(1L);
        refreshToken.setToken("refreshToken");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expirationTime);

        UserAuthenticationResponse userAuthResponse = new UserAuthenticationResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);
        when(userMapper.userToUserAuthenticationResponse(user)).thenReturn(userAuthResponse);

        ResponseEntity<UserAuthenticationResponse> output = authenticationService.signUp(signUpRequest);

        verify(userMapper).signUpRequestToUser(signUpRequest);
        verify(userRepository).save(any());
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).createRefreshToken(user);
        verify(userMapper).userToUserAuthenticationResponse(user);

        assertEquals(HttpStatus.OK, output.getStatusCode());
        assertThat(output.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("refreshToken");
            assertThat(cookie).contains("HttpOnly");
        });
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("jwt");
            assertThat(cookie).contains("HttpOnly");
        });
    }

    @Test
    void testSignUpEmailAlreadyExistsThrowsCorrectException() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email@email.com",
                "John",
                "Doe",
                "password"
        );
        User user = new User();
        user.setEmail(signUpRequest.email());

        when(userMapper.signUpRequestToUser(signUpRequest)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(SignUpException.class, () -> authenticationService.signUp(signUpRequest));

        verifyNoInteractions(jwtService);
        verifyNoInteractions(refreshTokenService);
        verify(userMapper, never()).userToUserAuthenticationResponse(any());
    }

    @Test
    void testSignIn() {
        SignInRequest signInRequest = new SignInRequest(
                "email@email.com",
                "password"
        );
        User user = new User();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");
        String jwt = "token";

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(signInRequest.email())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn(jwt);
        when(userMapper.userToUserAuthenticationResponse(user)).thenReturn(null);

        ResponseEntity<UserAuthenticationResponse> output = authenticationService.signIn(signInRequest);

        verify(authenticationManager).authenticate(any());
        verify(userRepository).save(user);
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).deleteUsersPreviousRefreshToken(user);
        verify(refreshTokenService).createRefreshToken(user);
        verify(userMapper).userToUserAuthenticationResponse(user);

        assertEquals(HttpStatus.OK, output.getStatusCode());
        assertThat(output.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("refreshToken");
            assertThat(cookie).contains("HttpOnly");
        });
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("jwt");
            assertThat(cookie).contains("HttpOnly");
        });
    }

    @Test
    void testSignInUserWithGivenEmailNotFoundThrowsCorrectException() {
        SignInRequest signInRequest = new SignInRequest(
                "email@email.com",
                "password"
        );

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(signInRequest.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.signIn(signInRequest));

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(signInRequest.email());

        verifyNoInteractions(jwtService);
        verifyNoInteractions(refreshTokenService);
        verifyNoInteractions(userMapper);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSignOut() {
        User user = new User();
        Authentication authentication = mock(Authentication.class);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        ResponseEntity<SignOutResponse> output = authenticationService.signOut();

        verify(authentication).getPrincipal();
        verify(refreshTokenService).deleteUsersPreviousRefreshToken(user);

        assertEquals(HttpStatus.OK, output.getStatusCode());
        assertThat(output.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("refreshToken");
            assertThat(cookie).contains("HttpOnly");
            assertThat(cookie).contains("Max-Age=0");
        });
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("jwt");
            assertThat(cookie).contains("HttpOnly");
            assertThat(cookie).contains("Max-Age=0");
        });
    }

    @Test
    void testRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie refreshTokenCookie = new Cookie("refreshToken", "refresh-token-value");
        RefreshToken token = new RefreshToken();
        User user = new User();
        token.setUser(user);
        UserAuthenticationResponse response = new UserAuthenticationResponse(
                1L,
                "email@email.com",
                "John",
                "Doe"
        );

        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(refreshTokenService.findByToken(refreshTokenCookie.getValue())).thenReturn(Optional.of(token));
        when(refreshTokenService.verifyExpirationDate(token)).thenReturn(token);
        when(userMapper.userToUserAuthenticationResponse(user)).thenReturn(response);
        when(jwtService.generateToken(user)).thenReturn("jwt-value");

        ResponseEntity<UserAuthenticationResponse> output = authenticationService.refreshToken(request);

        verify(request).getCookies();
        verify(refreshTokenService).findByToken(refreshTokenCookie.getValue());
        verify(refreshTokenService).verifyExpirationDate(token);
        verify(userMapper).userToUserAuthenticationResponse(user);
        verify(jwtService).generateToken(user);

        assertEquals(HttpStatus.OK, output.getStatusCode());
        assertThat(output.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("refreshToken");
            assertThat(cookie).contains("HttpOnly");
        });
        assertThat(output.getHeaders().get("Set-Cookie")).anySatisfy(cookie -> {
            assertThat(cookie).contains("jwt");
            assertThat(cookie).contains("HttpOnly");
        });
    }

    @Test
    void testRefreshTokenNoRefreshTokenCookieThrowsCorrectException() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(RefreshTokenException.class, () -> authenticationService.refreshToken(request));

        verifyNoInteractions(refreshTokenService);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(jwtService);
    }

    @Test
    void testRefreshTokenNotFoundInDatabaseThrowsCorrectException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie refreshTokenCookie = new Cookie("refreshToken", "refresh-token-value");

        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(refreshTokenService.findByToken(refreshTokenCookie.getValue())).thenReturn(Optional.empty());

        assertThrows(RefreshTokenException.class, () -> authenticationService.refreshToken(request));

        verifyNoInteractions(userMapper);
        verifyNoInteractions(jwtService);
        verify(refreshTokenService, never()).verifyExpirationDate(any());
    }
}