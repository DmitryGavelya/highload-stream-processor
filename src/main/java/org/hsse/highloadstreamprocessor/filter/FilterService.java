package org.hsse.highloadstreamprocessor.filter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class FilterService {
  private final FilteredSender sender;

  public void processMessage(Map<String, Object> message) {
    if (matchesFilter(message)) {
      sender.sendMessage(message);
    }
  }

  public boolean matchesFilter(Map<String, Object> message) {
    return true;
  }
}
