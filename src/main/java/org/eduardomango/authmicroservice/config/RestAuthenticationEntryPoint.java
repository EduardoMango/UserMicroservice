package org.eduardomango.authmicroservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Map<String, String> errorResponse = new HashMap<>();

        authException.printStackTrace();

        Throwable cause = authException;
        while (cause.getCause() != null && !(cause instanceof ExpiredJwtException)) {
            cause = cause.getCause();
        }

        if (cause instanceof ExpiredJwtException) {
            errorResponse.put("message", "Token has expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            errorResponse.put("message", "Authentication failed: " + authException.getMessage());
            errorResponse.put("error_type", authException.getClass().getSimpleName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // configure heading
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
