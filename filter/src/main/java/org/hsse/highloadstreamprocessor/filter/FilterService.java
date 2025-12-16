package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FilterService {
  private final FilterRepository repository;
  private final FilteredSender sender;

  public void processMessage(JsonNode message) throws JsonProcessingException {
    if (repository.loadFilter().accept(message)) {
      sender.sendMessage(message);
    }
  }
}
