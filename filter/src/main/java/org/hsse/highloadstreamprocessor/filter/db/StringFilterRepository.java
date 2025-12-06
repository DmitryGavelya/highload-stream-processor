package org.hsse.highloadstreamprocessor.filter.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StringFilterRepository extends JpaRepository<StringFilterEntity, String> {
}
