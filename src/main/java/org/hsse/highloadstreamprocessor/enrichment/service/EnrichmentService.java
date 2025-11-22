package org.hsse.highloadstreamprocessor.enrichment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hsse.highloadstreamprocessor.enrichment.entity.EnrichmentData;
import org.hsse.highloadstreamprocessor.enrichment.exception.NoEnrichmentDataException;
import org.hsse.highloadstreamprocessor.enrichment.kafka.EnrichmentSender;
import org.hsse.highloadstreamprocessor.enrichment.repository.EnrichmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import org.bson.Document;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnrichmentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EnrichmentRepository enrichmentRepository;

    @Autowired
    private EnrichmentSender enrichmentSender;

    private final ObjectMapper objectMapper;

    public void processMessage(Map<String, Object> message) throws JsonProcessingException {
        String key = message.get("query_value").toString();
        Map<String, Object> origin = objectMapper
                .readValue(
                        objectMapper.writeValueAsString(message.get("origin")),
                        new TypeReference<>() {}
                );

        if (enrichmentRepository.existsByQueryValue(key)) {
            EnrichmentData enrichmentData = enrichmentRepository
                    .findByQueryValue(key)
                    .getFirst();

            Document jsonData = mongoTemplate.findById(
                    enrichmentData.getId(),
                    Document.class,
                    "enrichment"
            );

            if (jsonData == null) {
                return;
            }

            for (var entry : jsonData.entrySet()) {
                origin.put(entry.getKey(), entry.getValue().toString());
            }

            enrichmentSender.sendMessage(origin);
        }
    }

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
