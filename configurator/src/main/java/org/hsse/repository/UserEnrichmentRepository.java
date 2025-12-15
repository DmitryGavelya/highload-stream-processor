package org.hsse.repository;

import org.hsse.entity.UserEnrichment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserEnrichmentRepository extends JpaRepository<UserEnrichment, Long> {

  List<UserEnrichment> findByUserId(String userId);

  boolean existsByUserIdAndColumnName(String userId, String columnName);

  @Modifying
  @Query("DELETE FROM UserEnrichment ue WHERE ue.userId = :userId AND ue.columnName = :columnName")
  void deleteByUserIdAndColumnName(@Param("userId") String userId, @Param("columnName") String columnName);

  @Modifying
  @Query("DELETE FROM UserEnrichment ue WHERE ue.userId = :userId")
  void deleteByUserId(@Param("userId") String userId);

  @Query("SELECT ue.columnName FROM UserEnrichment ue WHERE ue.userId = :userId")
  List<String> findColumnNamesByUserId(@Param("userId") String userId);
}