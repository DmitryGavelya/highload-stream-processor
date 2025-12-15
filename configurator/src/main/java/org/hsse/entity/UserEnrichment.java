package org.hsse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_enrichment")
@Getter
@Setter
public class UserEnrichment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "column_name", nullable = false)
  private String columnName;
}