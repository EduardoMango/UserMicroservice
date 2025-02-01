package org.eduardomango.authmicroservice.exceptions.handler;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationServiceException.class})
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationServiceException ex) {
        Map<String, String> response = new HashMap<>();

        response.put("error", ex.getMessage());
        response.put("status", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        if (ex.getCause() instanceof ExpiredJwtException) {
            response.put("message", "Token has expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<Map<String, String>> handleExpiredJwtException(ExpiredJwtException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token has expired");
        response.put("error", ex.getMessage());
        response.put("status", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
