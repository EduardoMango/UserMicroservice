package org.eduardomango.authmicroservice.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {


    @InjectMocks
    private JwtServiceImpl jwtService;

    private UserDetails userDetails;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final Long EXPIRATION = 86400000L; // 1 día en millisegundos

    @BeforeEach
    void setUp() {
        // Configurar el userDetails de prueba
        userDetails = new User(
                "test@example.com",
                "password",
                true,
                true,
                true,
                true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Inyectar valores usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws Exception {
        // Given
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        // Configure a token with immediate expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 0L);
        String token = jwtService.generateToken(userDetails);
        Thread.sleep(100); // Wait to ensure token is expired

        // When
        boolean isValid;
        try{
            isValid = jwtService.isTokenValid(token, userDetails);
        } catch (Exception _) {
            isValid = false;
        }

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForLockedAccount() {
        // Given
        UserDetails lockedUser = mock(UserDetails.class);
        when(lockedUser.getUsername()).thenReturn("test@example.com");
        when(lockedUser.isAccountNonLocked()).thenReturn(false);

        String token = jwtService.generateToken(lockedUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, lockedUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDisabledAccount() {
        // Given
        UserDetails disabledUser = mock(UserDetails.class);
        when(disabledUser.getUsername()).thenReturn("test@example.com");
        when(disabledUser.isAccountNonLocked()).thenReturn(true);
        when(disabledUser.isEnabled()).thenReturn(false);

        String token = jwtService.generateToken(disabledUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, disabledUser);

        // Then
        assertFalse(isValid);
    }
}