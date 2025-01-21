package org.eduardomango.authmicroservice.services;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @InjectMocks
    JpaUserDetailsService jpaUserDetailsService;

    @Mock
    CredentialsRepository credentialsRepository;

    private String username;

    @Test
    void loadUserByUsername_WithInvalidUsername_ShouldThrowUserNotFoundException() {
        //Given
        username = "invalidUsername";

        //Find empty
        when(credentialsRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            jpaUserDetailsService.loadUserByUsername(username);
        });

        //Verify the method was called
        verify(credentialsRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithValidUsername_ShouldReturnUserDetails() {
        //Given
        username = "validUsername";
        CredentialsEntity credentialsEntity = new CredentialsEntity();
        credentialsEntity.setUsername(username);
        credentialsEntity.setPassword("password");

        when(credentialsRepository.findByUsername(username))
                .thenReturn(Optional.of(credentialsEntity));

        //When
        UserDetails result = jpaUserDetailsService.loadUserByUsername(username);

        //Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(credentialsEntity.getPassword(), result.getPassword());

        verify(credentialsRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithNullUsername_ShouldThrowUsernameNotFoundException() {
        //Given
        username = null;
        //Find empty
        when(credentialsRepository.findByUsername(null))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            jpaUserDetailsService.loadUserByUsername(username);
        });

        //Verify the method was called
        verify(credentialsRepository).findByUsername(username);
    }
}
