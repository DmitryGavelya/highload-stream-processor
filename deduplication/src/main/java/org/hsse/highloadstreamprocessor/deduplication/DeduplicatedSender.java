package org.hsse.highloadstreamprocessor.deduplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DeduplicatedSender {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String deduplicatedTopic;

  public DeduplicatedSender(KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper objectMapper,
                            @Value("${deduplication.send-to}") String deduplicatedTopic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.deduplicatedTopic = deduplicatedTopic;
  }

  public void sendMessage(Map<String, Object> message) {
    try {
      String json = objectMapper.writeValueAsString(message);
      CompletableFuture<SendResult<String, String>> future =
              kafkaTemplate.send(deduplicatedTopic, json);
      log.info("Message sent to topic {}: {}", deduplicatedTopic, json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Converting message back to JSON failed, it shouldn't", e);
    }
  }
}

