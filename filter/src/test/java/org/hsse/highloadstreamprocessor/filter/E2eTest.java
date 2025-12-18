package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class E2eTest extends DatabaseSuit {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FilterRepository repository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @BeforeEach
  void addRules() {
    jdbcTemplate.update("CREATE TABLE IF NOT EXISTS filters (filter JSONB)");
    jdbcTemplate.update("INSERT INTO filters VALUES (?::jsonb)", """
            {
              "type": "and",
              "nested": [
                { "type": "=", "field": "int_field", "value": 15 },
                { "type": "=", "field": "string_field", "value": "test" },
                { "type": "=", "field": "bool_field", "value": true }
              ]
            }
            """);
  }

  @AfterEach
  void removeRules() {
    jdbcTemplate.update("DELETE FROM filters");
  }

  @Test
  void e2eTest() throws JsonProcessingException {
    final Map<String, Object> matching1 = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", true
    );
    final Map<String, Object> matching2 = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", true,
            "extra_field", "blah-blah-blah"
    );

    final List<Map<String, Object>> messages = List.of(
            matching1,
            matching2,
            Map.of(
                    "int_field", 15,
                    "string_field", "value_mismatch",
                    "bool_field", true
            ),
            Map.of(
                    "int_field", 15,
                    "string_field", "test",
                    "bool_field", "type_mismatch"
            ),
            Map.of(
                    "name_mismatch", 15,
                    "string_field", "test",
                    "bool_field", true
            )
    );

    for (final Map<String, Object> message : messages) {
      kafkaTemplate.send("source", objectMapper.writeValueAsString(message));
    }
    kafkaTemplate.flush();

    try (TestKafkaConsumer consumer = new TestKafkaConsumer(
            KAFKA.getBootstrapServers(), "filter_service")) {
      consumer.subscribe(List.of("filtered"));

      ConsumerRecords<String, String> sentMessages = consumer.poll();
      assertEquals(2, sentMessages.count());

      final List<Map<String, Object>> received =
              StreamSupport.stream(sentMessages.spliterator(), false).map(
                      record -> {
                        try {
                          return objectMapper.readValue(record.value(),
                                  new TypeReference<Map<String, Object>>() {
                                  });
                        } catch (JsonProcessingException e) {
                          throw new RuntimeException(e);
                        }
                      }
              ).toList();

      assertEquals(List.of(matching1, matching2), received);
    }
  }
}
