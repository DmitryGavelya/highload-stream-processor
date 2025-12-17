package org.hsse.service.filter;

import lombok.RequiredArgsConstructor;
import org.hsse.dto.UserFilterDto;
import org.hsse.entity.UserFilter;
import org.hsse.repository.UserFilterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFilterService {

  private final UserFilterRepository filterRepository;

  @Transactional
  public void saveUserFilter(String userId, UserFilterDto filterDto) {
    filterRepository.deleteByUserId(userId);
    UserFilter filter = new UserFilter();
    filter.setUserId(userId);
    filter.setJson(filterDto.getJson());
    filterRepository.save(filter);
  }

  public Optional<UserFilterDto> getUserFilter(String userId) {
    Optional<UserFilter> filterOpt = filterRepository.findByUserId(userId);
    return filterOpt.map(filter -> new UserFilterDto(filter.getJson()));
  }

  @Transactional
  public void deleteUserFilter(String userId) {
    filterRepository.deleteByUserId(userId);
  }

  public boolean hasUserFilter(String userId) {
    return filterRepository.existsByUserId(userId);
  }
}
