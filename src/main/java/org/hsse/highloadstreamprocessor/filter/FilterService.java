package org.hsse.highloadstreamprocessor.filter;

import lombok.AllArgsConstructor;
import org.hsse.highloadstreamprocessor.filter.db.BoolFilterRepository;
import org.hsse.highloadstreamprocessor.filter.db.IntFilterRepository;
import org.hsse.highloadstreamprocessor.filter.db.StringFilterRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FilterService {
  private final FilteredSender sender;

  private final StringFilterRepository stringFilterRepository;
  private final IntFilterRepository intFilterRepository;
  private final BoolFilterRepository boolFilterRepository;

  public void processMessage(Map<String, Object> message) {
    if (matchesFilter(message)) {
      sender.sendMessage(message);
    }
  }

  public boolean matchesFilter(Map<String, Object> message) {
    for (var filter : stringFilterRepository.findAll()) {
      if (!Objects.equals(message.get(filter.getFieldName()), filter.getValue())) {
        return false;
      }
    }

    for (var filter : intFilterRepository.findAll()) {
      if (!Objects.equals(message.get(filter.getFieldName()), filter.getValue())) {
        return false;
      }
    }

    for (var filter : boolFilterRepository.findAll()) {
      if (!Objects.equals(message.get(filter.getFieldName()), filter.getValue())) {
        return false;
      }
    }

    return true;
  }
}
