package org.eduardomango.authmicroservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * JWT authentication filter that intercepts every request to check for a valid token.
     * If a valid token is found, it sets the authentication context for the request.
     * If the token is expired, it returns a 401 Unauthorized response.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response to be sent.
     * @param filterChain The filter chain to continue processing the request.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (username != null && authentication == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
        }catch (ExpiredJwtException ex) {
            // Custom response for expired token
            System.out.println("Thrown exception: " + ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token has expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Determines whether a request should bypass the authentication filter.
     * Requests matching certain paths (such as login and registration) are excluded from filtering.
     *
     * @param request The incoming HTTP request.
     * @return True if the request should not be filtered, false otherwise.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/login") ||
                path.startsWith("/auth/authenticate") ||
                path.startsWith("/auth/exchange") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/login") ||
                path.startsWith("/register") ||
                path.equals("/auth/user");
    }
}
