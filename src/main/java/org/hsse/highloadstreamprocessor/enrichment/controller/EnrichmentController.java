package org.hsse.highloadstreamprocessor.enrichment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hsse.highloadstreamprocessor.enrichment.entity.EnrichmentData;
import org.hsse.highloadstreamprocessor.enrichment.exception.NoEnrichmentDataException;
import org.hsse.highloadstreamprocessor.enrichment.service.EnrichmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/enrichment")
@RequiredArgsConstructor
public class EnrichmentController {

    @Autowired
    private EnrichmentService enrichmentService;

    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable("id") String originId) {
        try {
            return ResponseEntity.ok(enrichmentService.getEnrichmentData(originId));
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.notFound().header("Bad request").build();
        }
    }

    @GetMapping("/enrich_data")
    public ResponseEntity<String> enrichData(@RequestBody String jsonData) {
        try {
            Map<String, Object> dataToEnrich = objectMapper.readValue(jsonData, new TypeReference<>() {});
            enrichmentService.processMessage(dataToEnrich);
            return ResponseEntity.ok("Successfully enriched!");
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.notFound().header("Bad request").build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("")
    public ResponseEntity<String> createEnrichmentData(@RequestBody String enrichmentJson) {
        try {
            enrichmentService.createEnrichmentData(enrichmentJson);
            return ResponseEntity.ok("Created");
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.badRequest().body("Bad request");
        }
    }

    @PutMapping("")
    public ResponseEntity<String> updateEnrichmentData(@RequestBody EnrichmentData enrichmentData) {
        try {
            enrichmentService.updateEnrichmentData(enrichmentData);
            return ResponseEntity.ok("Updated");
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.badRequest().body("Bad request");
        }
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteEnrichmentData(@RequestBody String id) {
        try {
            enrichmentService.deleteEnrichmentData(id);
            return ResponseEntity.ok("Deleted");
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.notFound().header("Bad request").build();
        }
    }
}
