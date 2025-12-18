package org.hsse.controller.filter;

import lombok.RequiredArgsConstructor;
import org.hsse.dto.UserFilterDto;
import org.hsse.service.filter.UserFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class UserFilterController {

  private final UserFilterService filterService;

  @PutMapping("/{userId}")
  public ResponseEntity<Void> saveFilter(
          @PathVariable String userId,
          @RequestBody String filterJson) {
    filterService.saveUserFilter(userId, new UserFilterDto(filterJson));
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}")
  public ResponseEntity<String> getFilter(@PathVariable String userId) {
    Optional<UserFilterDto> filterOpt = filterService.getUserFilter(userId);
    return filterOpt
            .map(UserFilterDto::getJson)
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
