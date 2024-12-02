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
import hr.adriaticanimation.saf_planner.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * Service for authenticating users
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${security.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register user.
     *
     * @param request - sign-up request containing necessary information.
     * @return - response with necessary details if sign-up was successful, otherwise error.
     */
    public ResponseEntity<UserAuthenticationResponse> signUp(SignUpRequest request) {
        User user = userMapper.signUpRequestToUser(request);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new SignUpException("User with given email already exists.");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        HttpHeaders headers = createHeaders(refreshToken.getToken(), refreshTokenExpirationTime);
        UserAuthenticationResponse response = userMapper.userToUserAuthenticationResponse(user);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * Sign user in.
     *
     * @param request - sign-in request containing user's email and password
     * @return - response with necessary details if sign-in was successful, otherwise error
     */
    public ResponseEntity<UserAuthenticationResponse> signIn(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = getUserFromSecurityContextHolder();
        user.setLastLogin(Timestamp.from(Instant.now()));
        user = userRepository.save(user);

        refreshTokenService.deleteUsersPreviousRefreshToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        HttpHeaders headers = createHeaders(refreshToken.getToken(), refreshTokenExpirationTime);
        UserAuthenticationResponse response = userMapper.userToUserAuthenticationResponse(user);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * Sign user out.
     *
     * @return - response notifying whether sign-out was successful
     */
    public ResponseEntity<SignOutResponse> signOut() {
        User user = getUserFromSecurityContextHolder();

        refreshTokenService.deleteUsersPreviousRefreshToken(user);
        SignOutResponse response = new SignOutResponse("User was logged out");
        HttpHeaders headers = createHeaders("", 0);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * Renew user's JWT token.
     * If the refresh token itself is close to expiring, it will be
     * renewed as well.
     *
     * @param request - request containing the refresh token
     * @return - authentication response
     */
    public ResponseEntity<UserAuthenticationResponse> refreshToken(HttpServletRequest request) {
        Cookie refreshTokenCookie = getRefreshTokenCookie(request)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found."));

        RefreshToken token = refreshTokenService
                .findByToken(refreshTokenCookie.getValue())
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        // Renews the token if it's close to expiry
        token = refreshTokenService.verifyExpirationDate(token);
        User user = token.getUser();
        UserAuthenticationResponse response = userMapper.userToUserAuthenticationResponse(user);
        HttpHeaders headers = createHeaders(token.getToken(), refreshTokenExpirationTime);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * Extract refresh token cookie from cookies.
     *
     * @param request - received http request
     * @return - optional of refresh token cookie
     */
    private Optional<Cookie> getRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst();
    }

    /**
     * Fetch user from the security context.
     *
     * @return - User entity
     */
    public User getUserFromSecurityContextHolder() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Create headers for the response
     *
     * @param token - Refresh token
     * @param expirationTime - Expiry for the refresh token
     * @return - headers containing the cookie that holds the refresh token
     */
    private HttpHeaders createHeaders(String token, long expirationTime) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = createCookie(token, expirationTime);
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    /**
     * Create a cookie for the refresh token.
     *
     * @param token - Refresh token string
     * @param expirationTime - amount of time the token will last
     * @return - the cookie for the refresh token
     */
    private ResponseCookie createCookie(String token, long expirationTime) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .path("/")
                .maxAge(expirationTime)
                .sameSite("Lax")
                .build();
    }
}
