package org.hsse.repository;

import org.hsse.entity.UserDeduplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDeduplicationRepository extends JpaRepository<UserDeduplication, Long> {

  List<UserDeduplication> findByUserId(String userId);

  boolean existsByUserIdAndColumnName(String userId, String columnName);

  @Modifying
  @Query("DELETE FROM UserDeduplication dc WHERE dc.userId = :userId AND dc.columnName = :columnName")
  void deleteByUserIdAndColumnName(@Param("userId") String userId, @Param("columnName") String columnName);

  @Modifying
  @Query("DELETE FROM UserDeduplication dc WHERE dc.userId = :userId")
  void deleteByUserId(@Param("userId") String userId);

  @Query("SELECT dc.columnName FROM UserDeduplication dc WHERE dc.userId = :userId")
  List<String> findColumnNamesByUserId(@Param("userId") String userId);
}