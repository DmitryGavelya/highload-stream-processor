package org.hsse.highloadstreamprocessor.deduplication.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "deduplication_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeduplicationConfigEntity {
  @Id
  @Column(name = "user_id")
  String userId;

  @Column(name = "time_window_seconds", nullable = false)
  Integer timeWindowSeconds;

  @Column(name = "excluded_fields")
  String excludedFields; // JSON array stored as string, e.g. ["timestamp", "requestId"]
}

