package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class SourceConsumer {
  private final FilterService filterService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = {"${filter.listen-to}"})
  public void consumeMessage(String message) throws JsonProcessingException {
    HashMap<String, Object> parsedMessage = objectMapper.readValue(message, new TypeReference<>() {
    });

    filterService.processMessage(parsedMessage);
  }
}
