package org.hsse.highloadstreamprocessor.filter.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "int_filters")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IntFilterEntity {
  @Id
  String fieldName;

  @NotNull
  @Column(name = "expected_value", nullable = false)
  Integer value;
}
