package org.hsse.highloadstreamprocessor.deduplication.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeduplicationConfigRepository extends JpaRepository<DeduplicationConfigEntity, String> {
  Optional<DeduplicationConfigEntity> findByUserId(String userId);
}

