package org.eduardomango.authmicroservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eduardomango.authmicroservice.config.JwtAuthenticationFilter;
import org.eduardomango.authmicroservice.config.SecurityTestConfig;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityTestConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilterChainProxy filterChainProxy;
    @Autowired
    private WebApplicationContext wac;

    @MockitoBean
    private AuthService authenticationService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest validAuthRequest;
    private CredentialsEntity validCredentials;
    private static final String TEST_JWT = "test.jwt.token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity()) //will perform all of the initial setup to integrate Spring Security with Spring MVC Test
                .build();
        validAuthRequest = new AuthRequest("testuser", "password123");
        validCredentials = new CredentialsEntity();
        validCredentials.setUsername("testuser");
        validCredentials.setPassword("hashedPassword");
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(AuthRequest.class)))
                .thenReturn(validCredentials);
        when(jwtService.generateToken(any(CredentialsEntity.class)))
                .thenReturn(TEST_JWT);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_JWT))
                .andDo(print());

        verify(authenticationService).authenticate(any(AuthRequest.class));
        verify(jwtService).generateToken(any(CredentialsEntity.class));
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldReturn401() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        verify(authenticationService).authenticate(any(AuthRequest.class));
        verify(jwtService, never()).generateToken(any(CredentialsEntity.class));
    }

    @Test
    void authenticate_WithMalformedRequest_ShouldReturn400() throws Exception {
        // Arrange
        String malformedJson = "{\"username\": \"testuser\"}"; // Missing password field

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(authenticationService, never()).authenticate(any(AuthRequest.class));
        verify(jwtService, never()).generateToken(any(CredentialsEntity.class));
    }

    @Test
    void authenticate_WithEmptyCredentials_ShouldReturn400() throws Exception {
        // Arrange
        AuthRequest emptyRequest = new AuthRequest("", "");

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(authenticationService, never()).authenticate(any(AuthRequest.class));
        verify(jwtService, never()).generateToken(any(CredentialsEntity.class));
    }
}
