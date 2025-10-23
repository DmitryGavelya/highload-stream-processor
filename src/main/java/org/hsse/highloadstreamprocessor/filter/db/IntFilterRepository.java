package org.hsse.highloadstreamprocessor.filter.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IntFilterRepository extends JpaRepository<IntFilterEntity, String> {
}
