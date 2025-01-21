package org.eduardomango.authmicroservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eduardomango.authmicroservice.models.Enum.UserProfile;

import java.util.HashSet;
import java.util.Set;


@Getter
@Entity
@Table(name = "profiles")
@NoArgsConstructor

public class ProfileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long profile_id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private UserProfile profile;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(
          name = "profile_roles",
          joinColumns = @JoinColumn(name = "profile_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private final Set<RoleEntity> roles = new HashSet<>();



  public Set<RoleEntity> getRoles() {
    return this.roles;
  }

  public ProfileEntity(UserProfile profile) {
    this.profile = profile;
  }

  public void addRole(RoleEntity role) {
    this.roles.add(role);
  }
}
