package org.hsse.highloadstreamprocessor.enrichment.service;

import org.hsse.highloadstreamprocessor.enrichment.entity.EnrichmentData;
import org.hsse.highloadstreamprocessor.enrichment.exception.NoEnrichmentDataException;
import org.hsse.highloadstreamprocessor.enrichment.repository.EnrichmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import org.bson.Document;

import java.util.Objects;

@Service
public class EnrichmentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EnrichmentRepository enrichmentRepository;

    public String getEnrichmentData(String originId) {
        String enrichmentId = enrichmentRepository.findByOriginalId(originId)
                .orElseThrow(() -> new NoEnrichmentDataException(String.format("Failed find id = %s", originId)))
                .getId();

        return Objects.requireNonNull(mongoTemplate.findById(enrichmentId, Document.class, "enrichment")).toJson();
    }

    public void createEnrichmentData(String json) {
        Document document = Document.parse(json);
        mongoTemplate.insert(document, "enrichment");
    }

    public void updateEnrichmentData(EnrichmentData enrichmentData) {
        if (!enrichmentRepository.existsById(enrichmentData.getId())) {
            throw new NoEnrichmentDataException(String.format("Failed find id = %s", enrichmentData.getId()));
        }

        enrichmentRepository.save(enrichmentData);
    }

    public void deleteEnrichmentData(String originalId) {
        if (!enrichmentRepository.existsByOriginalId(originalId)) {
            throw new NoEnrichmentDataException(String.format("Failed find id = %s", originalId));
        }

        enrichmentRepository.deleteByOriginalId(originalId);
    }
}
