package hr.adriaticanimation.saf_planner.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntrypoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException.getCause() instanceof io.jsonwebtoken.ExpiredJwtException) {
            log.warn("Expired JWT: {}", authException.getMessage());
        } else {
            log.error("Authentication error: {}", authException.getMessage());
        }

        // Respond with 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String jsonResponse = "{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}";
        response.getWriter().write(jsonResponse);

    }
}
