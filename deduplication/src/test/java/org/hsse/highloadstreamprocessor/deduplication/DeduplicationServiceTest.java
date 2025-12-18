package org.hsse.highloadstreamprocessor.deduplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hsse.highloadstreamprocessor.deduplication.db.DeduplicationConfigEntity;
import org.hsse.highloadstreamprocessor.deduplication.db.DeduplicationConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
@Testcontainers
class DeduplicationServiceTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @Container
  static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
      .withExposedPorts(6379);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
  }

  @Autowired
  private DeduplicationService deduplicationService;

  @Autowired
  private DeduplicationConfigRepository configRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @BeforeEach
  void setUp() {
    configRepository.deleteAll();
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  @Test
  void testFirstMessageIsNotDuplicate() {
    Map<String, Object> message = new HashMap<>();
    message.put("userId", "user1");
    message.put("data", "test message");

    configRepository.save(new DeduplicationConfigEntity("user1", 300, null));

    boolean isDuplicate = deduplicationService.isDuplicate(message);

    assertFalse(isDuplicate, "First message should not be duplicate");
  }

  @Test
  void testSecondIdenticalMessageIsDuplicate() {
    Map<String, Object> message = new HashMap<>();
    message.put("userId", "user1");
    message.put("data", "test message");

    configRepository.save(new DeduplicationConfigEntity("user1", 300, null));

    deduplicationService.isDuplicate(message);
    boolean isDuplicate = deduplicationService.isDuplicate(message);

    assertTrue(isDuplicate, "Second identical message should be duplicate");
  }

  @Test
  void testDifferentMessagesAreNotDuplicates() {
    Map<String, Object> message1 = new HashMap<>();
    message1.put("userId", "user1");
    message1.put("data", "test message 1");

    Map<String, Object> message2 = new HashMap<>();
    message2.put("userId", "user1");
    message2.put("data", "test message 2");

    configRepository.save(new DeduplicationConfigEntity("user1", 300, null));

    deduplicationService.isDuplicate(message1);
    boolean isDuplicate = deduplicationService.isDuplicate(message2);

    assertFalse(isDuplicate, "Different messages should not be duplicates");
  }

  @Test
  void testExcludedFieldsAreIgnored() {
    Map<String, Object> message1 = new HashMap<>();
    message1.put("userId", "user1");
    message1.put("data", "test message");
    message1.put("timestamp", "2024-01-01T10:00:00");

    Map<String, Object> message2 = new HashMap<>();
    message2.put("userId", "user1");
    message2.put("data", "test message");
    message2.put("timestamp", "2024-01-01T10:01:00");

    configRepository.save(new DeduplicationConfigEntity("user1", 300, "[\"timestamp\"]"));

    deduplicationService.isDuplicate(message1);
    boolean isDuplicate = deduplicationService.isDuplicate(message2);

    assertTrue(isDuplicate, "Messages with different excluded fields should be duplicates");
  }
}

