package org.hsse.highloadstreamprocessor.deduplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
@Slf4j
public class FilteredConsumer {
  private final DeduplicationService deduplicationService;
  private final DeduplicatedSender sender;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = {"${deduplication.listen-to}"})
  public void consumeMessage(String message) throws JsonProcessingException {
    log.info("Received message: {}", message);

    HashMap<String, Object> parsedMessage = objectMapper.readValue(message, new TypeReference<>() {
    });

    // Проверяем, является ли сообщение дубликатом
    if (!deduplicationService.isDuplicate(parsedMessage)) {
      // Если не дубликат, отправляем дальше
      sender.sendMessage(parsedMessage);
    } else {
      log.info("Duplicate message ignored: {}", message);
    }
  }
}

