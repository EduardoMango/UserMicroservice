package org.eduardomango.authmicroservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@Table(name = "credentials")
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true,length = 50)
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must have at least 6 characters")
    @Column(unique = true, nullable = false, length = 50)
    String username;
//    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Username must have at least 6 characters")
//    @Column(nullable = false)
    String password;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "credentials_roles",
            joinColumns = @JoinColumn(name = "credential_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private final Set<RoleEntity> roles = new HashSet<>();

    @Column(name = "oauth2_provider", nullable = true)
    private String oauth2Provider;

    @Column(name = "oauth2_provider_id", nullable = true, unique = true)
    private String oauth2ProviderId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        profile.getRoles().forEach(rol -> authorities.add(new SimpleGrantedAuthority(rol.getRole().name())));
        roles.forEach(rol -> authorities.add(new SimpleGrantedAuthority(rol.getRole().name())));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
