package org.hsse.highloadstreamprocessor.enrichment.controller;

import org.hsse.highloadstreamprocessor.enrichment.entity.EnrichmentData;
import org.hsse.highloadstreamprocessor.enrichment.exception.NoEnrichmentDataException;
import org.hsse.highloadstreamprocessor.enrichment.service.EnrichmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/enrichment")
public class EnrichmentController {

    @Autowired
    private EnrichmentService enrichmentService;

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable("id") String originId) {
        try {
            return ResponseEntity.ok(enrichmentService.getEnrichmentData(originId));
        } catch (NoEnrichmentDataException e) {
            return ResponseEntity.notFound().header("Bad request").build();
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
