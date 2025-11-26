package org.hsse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hsse.service.filter.FilterOperator;

@Entity
@Table(name = "user_filters")
@Getter
@Setter
public class UserFilter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "filter_name", nullable = false)
  private String filterName;

  @Column(name = "field", nullable = false)
  private String field;

  @Enumerated(EnumType.STRING)
  @Column(name = "operator", nullable = false)
  private FilterOperator operator;

  @Column(name = "value")
  private String value;

}
