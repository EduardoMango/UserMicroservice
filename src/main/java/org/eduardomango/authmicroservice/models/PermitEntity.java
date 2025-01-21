package org.eduardomango.authmicroservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.eduardomango.authmicroservice.models.Enum.UserPermit;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "permits")
@AllArgsConstructor
public class PermitEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private UserPermit permit;

  @Column(nullable = false,length = 50)
  private String description;

}
