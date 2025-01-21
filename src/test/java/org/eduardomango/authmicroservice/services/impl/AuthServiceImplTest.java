package org.eduardomango.authmicroservice.services.impl;

import org.eduardomango.authmicroservice.exceptions.InvalidParametersException;
import org.eduardomango.authmicroservice.exceptions.InvalidPasswordException;
import org.eduardomango.authmicroservice.exceptions.InvalidUsernameException;
import org.eduardomango.authmicroservice.exceptions.UserNotFoundException;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthRequest validAuthRequest;
    private CredentialsEntity validCredentials;
    private Long validId;

    @BeforeEach
    void setUp() {

        validAuthRequest = new AuthRequest("testuser", "password123");
        validCredentials = new CredentialsEntity(); // Set necessary fields for your entity
        validCredentials.setUsername("testuser");

    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnCredentialsEntity() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(validAuthRequest.username(), validAuthRequest.password()));

        when(credentialsRepository.findByUsername(validAuthRequest.username()))
                .thenReturn(Optional.of(validCredentials));

        // When
        CredentialsEntity result = authService.authenticate(validAuthRequest);

        // Then
        assertNotNull(result);
        assertEquals(validCredentials.getUsername(), result.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(credentialsRepository).findByUsername(validAuthRequest.username());
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When/Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(validAuthRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(credentialsRepository, never()).findByUsername(any());
    }

    @Test
    void authenticate_WithNonExistentUser_ShouldThrowNoSuchElementException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(validAuthRequest.username(), validAuthRequest.password()));

        when(credentialsRepository.findByUsername(validAuthRequest.username()))
                .thenReturn(Optional.empty());

        // When/Then
        assertThrows(NoSuchElementException.class, () -> {
            authService.authenticate(validAuthRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(credentialsRepository).findByUsername(validAuthRequest.username());
    }

    @Test
    void authenticate_WithNullInput_ShouldThrowNullPointerException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> {
            authService.authenticate(null);
        });

        verify(authenticationManager, never()).authenticate(any());
        verify(credentialsRepository, never()).findByUsername(any());
    }

    @Test
    void updateCredentials_WithValidUsernameAndPassword_ShouldUpdateBoth() {
        // Given
        validId = 1L;
        validCredentials.setPassword("testPassword");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        AuthRequest newCredentials = new AuthRequest("newusername", "newpassword");
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));
        when(credentialsRepository.save(any(CredentialsEntity.class))).thenReturn(validCredentials);

        // When
        authService.updateCredentials(validId, newCredentials);

        // Then
        verify(credentialsRepository).findById(validId);
        verify(passwordEncoder).encode("newpassword");
        verify(credentialsRepository).save(argThat(saved ->
                saved.getUsername().equals("newusername") &&
                        saved.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void updateCredentials_WithValidUsername_ShouldUpdateOnlyUsername() {
        // Given
        validId = 1L;
        validCredentials.setPassword("testPassword");
        AuthRequest newCredentials = new AuthRequest("newusername", null);
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));

        // When
        authService.updateCredentials(validId, newCredentials);

        // Then
        verify(credentialsRepository).findById(validId);
        verify(passwordEncoder, never()).encode(any());
        verify(credentialsRepository).save(argThat(saved ->
                saved.getUsername().equals("newusername") &&
                        saved.getPassword().equals("testPassword")
        ));
    }

    @Test
    void updateCredentials_WithValidPassword_ShouldUpdateOnlyPassword() {
        // Given
        validId = 1L;
        validCredentials.setPassword("testPassword");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        AuthRequest newCredentials = new AuthRequest(null, "newpassword");
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));

        // When
        authService.updateCredentials(validId, newCredentials);

        // Then
        verify(credentialsRepository).findById(validId);
        verify(passwordEncoder).encode("newpassword");
        verify(credentialsRepository).save(argThat(saved -> {

            System.out.println("Expected username: testUser");
            System.out.println("Actual username: " + saved.getUsername());
            System.out.println("Expected password: encodedPassword");
            System.out.println("Actual password: " + saved.getPassword());

            return saved.getUsername().equals("testuser") &&
                    saved.getPassword().equals("encodedPassword");
        }));
    }

    @Test
    void updateCredentials_WithInvalidId_ShouldThrowUserNotFoundException() {
        // Given
        validId = 1L;
        AuthRequest newCredentials = new AuthRequest("newusername", "newpassword");
        when(credentialsRepository.findById(validId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
                authService.updateCredentials(validId, newCredentials)
        );

        verify(credentialsRepository).findById(validId);
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void updateCredentials_WithShortUsername_ShouldThrowInvalidUsernameException() {
        // Given
        validId = 1L;
        AuthRequest newCredentials = new AuthRequest("short", "newpassword");
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));

        // When/Then
        assertThrows(InvalidUsernameException.class, () ->
                authService.updateCredentials(validId, newCredentials)
        );

        verify(credentialsRepository).findById(validId);
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void updateCredentials_WithShortPassword_ShouldThrowInvalidPasswordException() {
        // Given
        validId = 1L;
        AuthRequest newCredentials = new AuthRequest("newusername", "short");
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));

        // When/Then
        assertThrows(InvalidPasswordException.class, () ->
                authService.updateCredentials(validId, newCredentials)
        );

        verify(credentialsRepository).findById(validId);
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void updateCredentials_WithNullValues_ShouldThrowInvalidParametersException() {
        // Given
        validId = 1L;
        AuthRequest newCredentials = new AuthRequest(null, null);
        when(credentialsRepository.findById(validId)).thenReturn(Optional.of(validCredentials));

        // When/Then
        assertThrows(InvalidParametersException.class, () ->
                authService.updateCredentials(validId, newCredentials)
        );

        verify(credentialsRepository).findById(validId);
        verify(credentialsRepository, never()).save(any());
    }
}
