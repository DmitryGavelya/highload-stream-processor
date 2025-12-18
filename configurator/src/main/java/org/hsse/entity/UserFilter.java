package org.hsse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_filters")
@Getter
@Setter
public class UserFilter {
  @Id
  @Column(name = "user_id")
  private String userId;

  @Column(name = "filter")
  @JdbcTypeCode(SqlTypes.JSON)
  private String json;
}
