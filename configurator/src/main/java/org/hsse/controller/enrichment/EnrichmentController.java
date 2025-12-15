package org.hsse.controller.enrichment;

import lombok.RequiredArgsConstructor;
import org.hsse.dto.DeduplicationRequestsDto;
import org.hsse.dto.EnrichmentConfigDto;
import org.hsse.service.enrichment.EnrichmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/enrichment")
@RequiredArgsConstructor
public class EnrichmentController {

  private final EnrichmentService enrichmentService;

  @PostMapping("/{userId}/columns/{columnName}")
  public ResponseEntity<Void> addColumn(
      @PathVariable String userId,
      @PathVariable String columnName) {

    enrichmentService.addEnrichmentColumn(userId, columnName);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{userId}/columns/batch")
  public ResponseEntity<Void> addColumns(
      @PathVariable String userId,
      @RequestBody DeduplicationRequestsDto request) {

    enrichmentService.addEnrichmentColumns(userId, request.getColumns());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{userId}/columns/{columnName}")
  public ResponseEntity<Void> removeColumn(
      @PathVariable String userId,
      @PathVariable String columnName) {

    enrichmentService.removeEnrichmentColumn(userId, columnName);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/columns")
  public ResponseEntity<EnrichmentConfigDto> getColumns(@PathVariable String userId) {
    List<String> columns = enrichmentService.getEnrichmentColumns(userId);
    EnrichmentConfigDto config = new EnrichmentConfigDto(userId, columns);
    return ResponseEntity.ok(config);
  }

  @DeleteMapping("/{userId}/columns")
  public ResponseEntity<Void> clearColumns(@PathVariable String userId) {
    enrichmentService.clearEnrichmentColumns(userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/columns/{columnName}/exists")
  public ResponseEntity<Boolean> columnExists(
      @PathVariable String userId,
      @PathVariable String columnName) {

    boolean exists = enrichmentService.hasEnrichmentColumn(userId, columnName);
    return ResponseEntity.ok(exists);
  }
}