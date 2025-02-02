package org.eduardomango.authmicroservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eduardomango.authmicroservice.models.Enum.UserRole;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id", nullable = false)
  private Long id;


  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private UserRole role;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(
      name = "role_permits",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permit_id"))
  private final Set<PermitEntity> permits = new HashSet<>();

  public RoleEntity(UserRole name) {
    this.role = name;
  }

    public void addPermit(PermitEntity permit) {
    this.permits.add(permit);
  }
}
