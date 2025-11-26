package org.hsse.service.deduplication;

import lombok.RequiredArgsConstructor;
import org.hsse.entity.UserDeduplication;
import org.hsse.repository.UserDeduplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeduplicationService {

  private final UserDeduplicationRepository columnRepository;

  @Transactional
  public void addDeduplicationColumn(String userId, String columnName) {
    if (!columnRepository.existsByUserIdAndColumnName(userId, columnName)) {
      UserDeduplication column = new UserDeduplication();
      column.setUserId(userId);
      column.setColumnName(columnName);
      columnRepository.save(column);
    }
  }

  @Transactional
  public void removeDeduplicationColumn(String userId, String columnName) {
    columnRepository.deleteByUserIdAndColumnName(userId, columnName);
  }

  public List<String> getDeduplicationColumns(String userId) {
    return columnRepository.findColumnNamesByUserId(userId);
  }

  @Transactional
  public void clearDeduplicationColumns(String userId) {
    columnRepository.deleteByUserId(userId);
  }

  public boolean hasDeduplicationColumn(String userId, String columnName) {
    return columnRepository.existsByUserIdAndColumnName(userId, columnName);
  }
}
