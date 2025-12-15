package org.hsse.controller.filter;

import lombok.RequiredArgsConstructor;
import org.hsse.dto.UserFilterDto;
import org.hsse.service.filter.UserFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class UserFilterController {

  private final UserFilterService filterService;

  @PutMapping("/{userId}")
  public ResponseEntity<Void> saveFilter(
      @PathVariable String userId,
      @RequestBody UserFilterDto filterDto) {

    filterService.saveUserFilter(userId, filterDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserFilterDto> getFilter(@PathVariable String userId) {
    Optional<UserFilterDto> filterOpt = filterService.getUserFilter(userId);

    return filterOpt
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteFilter(@PathVariable String userId) {
    filterService.deleteUserFilter(userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/exists")
  public ResponseEntity<Boolean> hasFilter(@PathVariable String userId) {
    boolean exists = filterService.hasUserFilter(userId);
    return ResponseEntity.ok(exists);
  }
}