package org.hsse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_deduplication")

public class UserDeduplication {
  @Getter
  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Getter
  @Setter
  @Column(name = "user_id", nullable = false)
  private String userId;
  @Getter
  @Setter
  @Column(name = "excluded_fields", nullable = false)
  private String columnName;
}
