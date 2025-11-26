package org.hsse.controller.deduplication;

import lombok.RequiredArgsConstructor;
import org.hsse.dto.DeduplicationRequestDto;
import org.hsse.dto.DeduplicationRequestsDto;
import org.hsse.service.deduplication.DeduplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deduplication")
@RequiredArgsConstructor
public class DeduplicationController {

  private final DeduplicationService deduplicationService;

  @PostMapping("/{userId}/columns/{columnName}")
  public ResponseEntity<Void> addColumn(
      @PathVariable String userId,
      @PathVariable String columnName) {

    deduplicationService.addDeduplicationColumn(userId, columnName);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{userId}/columns/batch")
  public ResponseEntity<Void> addColumns(
      @PathVariable String userId,
      @RequestBody DeduplicationRequestsDto request) {

    deduplicationService.addDeduplicationColumns(userId, request.getColumns());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{userId}/columns/{columnName}")
  public ResponseEntity<Void> removeColumn(
      @PathVariable String userId,
      @PathVariable String columnName) {

    deduplicationService.removeDeduplicationColumn(userId, columnName);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/columns")
  public ResponseEntity<DeduplicationRequestDto> getColumns(@PathVariable String userId) {
    List<String> columns = deduplicationService.getDeduplicationColumns(userId);
    DeduplicationRequestDto config = new DeduplicationRequestDto(userId, columns);
    return ResponseEntity.ok(config);
  }

  @DeleteMapping("/{userId}/columns")
  public ResponseEntity<Void> clearColumns(@PathVariable String userId) {
    deduplicationService.clearDeduplicationColumns(userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/columns/{columnName}/exists")
  public ResponseEntity<Boolean> columnExists(
      @PathVariable String userId,
      @PathVariable String columnName) {

    boolean exists = deduplicationService.hasDeduplicationColumn(userId, columnName);
    return ResponseEntity.ok(exists);
  }
}
