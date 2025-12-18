package main.java.org.hsse.highloadstreamprocessor.deduplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hsse.highloadstreamprocessor.deduplication.db.DeduplicationConfigEntity;
import org.hsse.highloadstreamprocessor.deduplication.db.DeduplicationConfigRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeduplicationService {
  private final RedisTemplate<String, String> redisTemplate;
  private final DeduplicationConfigRepository configRepository;
  private final ObjectMapper objectMapper;

  private static final int DEFAULT_TIME_WINDOW_SECONDS = 300; // 5 минут по умолчанию
  private static final String REDIS_KEY_PREFIX = "dedup:";

  public boolean isDuplicate(Map<String, Object> message) {
    // Получаем userId из сообщения (предполагаем, что он там есть)
    String userId = extractUserId(message);

    // Получаем конфигурацию дедупликации для пользователя
    DeduplicationConfigEntity config = configRepository.findByUserId(userId)
        .orElse(new DeduplicationConfigEntity(userId, DEFAULT_TIME_WINDOW_SECONDS, null));

    // Фильтруем поля для дедупликации
    Map<String, Object> filteredMessage = filterMessage(message, config.getExcludedFields());

    // Вычисляем хеш отфильтрованного сообщения
    String hash = calculateHash(filteredMessage);
    String redisKey = REDIS_KEY_PREFIX + userId + ":" + hash;

    // Проверяем, есть ли хеш в Redis
    Boolean exists = redisTemplate.hasKey(redisKey);

    if (Boolean.TRUE.equals(exists)) {
      log.info("Duplicate message detected for userId={}, hash={}", userId, hash);
      return true;
    }

    // Сохраняем хеш в Redis с TTL
    redisTemplate.opsForValue().set(
        redisKey,
        "1",
        config.getTimeWindowSeconds(),
        TimeUnit.SECONDS
    );

    log.info("New unique message for userId={}, hash={}", userId, hash);
    return false;
  }

  private String extractUserId(Map<String, Object> message) {
    // Пытаемся извлечь userId из сообщения
    Object userIdObj = message.get("userId");
    if (userIdObj != null) {
      return userIdObj.toString();
    }
    // Если userId нет, используем "default"
    return "default";
  }

  private Map<String, Object> filterMessage(Map<String, Object> message, String excludedFieldsJson) {
    if (excludedFieldsJson == null || excludedFieldsJson.isEmpty()) {
      return message;
    }

    try {
      List<String> excludedFields = objectMapper.readValue(excludedFieldsJson, new TypeReference<>() {});
      return message.entrySet().stream()
          .filter(entry -> !excludedFields.contains(entry.getKey()))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    } catch (JsonProcessingException e) {
      log.error("Failed to parse excluded fields JSON: {}", excludedFieldsJson, e);
      return message;
    }
  }

  private String calculateHash(Map<String, Object> message) {
    try {
      // Сортируем ключи для консистентного хеширования
      TreeMap<String, Object> sortedMessage = new TreeMap<>(message);
      String json = objectMapper.writeValueAsString(sortedMessage);

      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(json.getBytes());

      // Конвертируем в hex строку
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to calculate message hash", e);
    }
  }
}

