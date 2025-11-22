package org.hsse.highloadstreamprocessor.enrichment.repository;

import org.hsse.highloadstreamprocessor.enrichment.entity.EnrichmentData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrichmentRepository extends MongoRepository<EnrichmentData, String> {

    Optional<EnrichmentData> findByOriginalId(String originalId);

    boolean existsByOriginalId(String originalId);

    boolean existsByQueryValue(String queryValue);

    List<EnrichmentData> findByStatus(String status);

    List<EnrichmentData> findByQueryValue(String queryValue);

    void deleteByOriginalId(String originalId);
}
