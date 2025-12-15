package org.hsse.service.enrichment;

import lombok.RequiredArgsConstructor;
import org.hsse.entity.UserEnrichment;
import org.hsse.repository.UserEnrichmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrichmentService {

  private final UserEnrichmentRepository enrichmentRepository;

  @Transactional
  public void addEnrichmentColumn(String userId, String columnName) {
    if (!enrichmentRepository.existsByUserIdAndColumnName(userId, columnName)) {
      UserEnrichment column = new UserEnrichment();
      column.setUserId(userId);
      column.setColumnName(columnName);
      enrichmentRepository.save(column);
    }
  }

  @Transactional
  public void addEnrichmentColumns(String userId, List<String> columns) {
    for (String columnName : columns) {
      if (!enrichmentRepository.existsByUserIdAndColumnName(userId, columnName)) {
        UserEnrichment column = new UserEnrichment();
        column.setUserId(userId);
        column.setColumnName(columnName);
        enrichmentRepository.save(column);
      }
    }
  }

  @Transactional
  public void removeEnrichmentColumn(String userId, String columnName) {
    enrichmentRepository.deleteByUserIdAndColumnName(userId, columnName);
  }

  public List<String> getEnrichmentColumns(String userId) {
    return enrichmentRepository.findColumnNamesByUserId(userId);
  }

  @Transactional
  public void clearEnrichmentColumns(String userId) {
    enrichmentRepository.deleteByUserId(userId);
  }

  public boolean hasEnrichmentColumn(String userId, String columnName) {
    return enrichmentRepository.existsByUserIdAndColumnName(userId, columnName);
  }
}
