package hr.adriaticanimation.saf_planner.configuration;

public class SecurityConstants {

    private SecurityConstants() {}

    protected static final String[] PUBLIC_URLS = {
            "/api/auth/sign-up",
            "/api/auth/sign-in",
            "/api/auth/refresh-token",

            // Open-API
            "/swagger-ui/**",
            "/api-docs",
            "/v3/api-docs/**",

            // TESTING
            "/api/test/open"
    };
}
