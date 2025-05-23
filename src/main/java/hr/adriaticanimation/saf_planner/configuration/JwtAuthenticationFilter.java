package hr.adriaticanimation.saf_planner.configuration;

import hr.adriaticanimation.saf_planner.services.authentication.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final List<String> publicUrls = List.of(SecurityConstants.PUBLIC_URLS);

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return publicUrls.stream().anyMatch(url -> new AntPathRequestMatcher(url).matches(request));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String jwt;
        final String username;

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie jwtCookie = Arrays.stream(cookies)
                        .filter(cookie -> "jwt".equals(cookie.getName()))
                        .findFirst()
                        .orElse(null);
                if (jwtCookie != null) {
                    jwt = jwtCookie.getValue();
                    username = jwtService.extractUsername(jwt);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}