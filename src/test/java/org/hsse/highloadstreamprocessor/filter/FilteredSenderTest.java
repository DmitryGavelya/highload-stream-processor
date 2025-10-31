package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// this test is an example and a check for config working properly
// TODO: remove it from production ready code
@SpringBootTest(
        classes = {FilteredSender.class},
        properties = {
                "filter.send-to=test_filtered",
                "spring.kafka.consumer.group-id=test_filter_service",
                "debug=true"
        }
)
@Import({KafkaAutoConfiguration.class})
@Testcontainers
@ActiveProfiles("test")
@EnableAutoConfiguration
public class FilteredSenderTest extends DatabaseSuit {
  @Autowired
  private FilteredSender sender;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  public void testSendMessage() {
    final Map<String, Object> toSend = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", true
    );

    assertDoesNotThrow(() -> sender.sendMessage(toSend));
    kafkaTemplate.flush();

    try (TestKafkaConsumer consumer = new TestKafkaConsumer(
            KAFKA.getBootstrapServers(), "test_filter_service")) {
      consumer.subscribe(List.of("test_filtered"));

      ConsumerRecords<String, String> sentMessages = consumer.poll();
      assertEquals(1, sentMessages.count());
      sentMessages.iterator().forEachRemaining(
              record -> {
                Map<String, Object> sent = null;
                try {
                  sent = objectMapper.readValue(record.value(), new TypeReference<>() {
                  });
                } catch (JsonProcessingException e) {
                  fail(e);
                }
                assertEquals(toSend, sent);
              }
      );
    }
  }
}
