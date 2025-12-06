package org.hsse.highloadstreamprocessor.filter.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoolFilterRepository extends JpaRepository<BoolFilterEntity, String> {
}
