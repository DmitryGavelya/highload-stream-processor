package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FilteredSender {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String filteredTopic;

  public FilteredSender(KafkaTemplate<String, String> kafkaTemplate,
                        ObjectMapper objectMapper,
                        @Value("filter.send-to") String filteredTopic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.filteredTopic = filteredTopic;
  }

  public void sendMessage(Map<String, Object> message) {
    try {
      CompletableFuture<SendResult<String, String>> future =
              kafkaTemplate.send(filteredTopic, objectMapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Converting message back to JSON failed, it shouldn't", e);
    }
  }
}
