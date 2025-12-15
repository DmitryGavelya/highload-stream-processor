package org.hsse.repository;

import org.hsse.entity.UserFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {

  Optional<UserFilter> findByUserId(String userId);

  @Modifying
  @Query("DELETE FROM UserFilter uf WHERE uf.userId = :userId")
  void deleteByUserId(@Param("userId") String userId);

  boolean existsByUserId(String userId);
}