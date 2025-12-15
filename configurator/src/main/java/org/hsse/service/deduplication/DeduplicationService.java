package org.hsse.service.deduplication;

import org.hsse.entity.UserDeduplication;
import org.hsse.repository.UserDeduplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DeduplicationService {

  private final UserDeduplicationRepository deduplicationRepository;

  public DeduplicationService(UserDeduplicationRepository deduplicationRepository) {
    this.deduplicationRepository = deduplicationRepository;
  }

  @Transactional
  public void addDeduplicationColumns(String userId, List<String> columns) {
    for (String columnName : columns) {
      if (!deduplicationRepository.existsByUserIdAndColumnName(userId, columnName)) {
        UserDeduplication column = new UserDeduplication();
        column.setUserId(userId);
        column.setColumnName(columnName);
        deduplicationRepository.save(column);
      }
    }
  }


  @Transactional
  public void addDeduplicationColumn(String userId, String columnName) {
    if (!deduplicationRepository.existsByUserIdAndColumnName(userId, columnName)) {
      UserDeduplication column = new UserDeduplication();
      column.setUserId(userId);
      column.setColumnName(columnName);
      deduplicationRepository.save(column);
    }
  }

  @Transactional
  public void removeDeduplicationColumn(String userId, String columnName) {
    deduplicationRepository.deleteByUserIdAndColumnName(userId, columnName);
  }

  public List<String> getDeduplicationColumns(String userId) {
    return deduplicationRepository.findColumnNamesByUserId(userId);
  }

  @Transactional
  public void clearDeduplicationColumns(String userId) {
    deduplicationRepository.deleteByUserId(userId);
  }

  public boolean hasDeduplicationColumn(String userId, String columnName) {
    return deduplicationRepository.existsByUserIdAndColumnName(userId, columnName);
  }
}
