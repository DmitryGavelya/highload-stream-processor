package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;

// this test is an example and a check for config working properly
// TODO: remove it from production ready code
@SpringBootTest(
        classes = {SourceConsumer.class},
        properties = {
                "filter.listen-to=test_filter",
                "spring.kafka.consumer.group-id=test_filter_service",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        }
)
@Import({KafkaAutoConfiguration.class})
@Testcontainers
@ActiveProfiles("test")
@EnableAutoConfiguration
class SourceConsumerTest extends DatabaseSuit {
  @MockitoBean
  private FilterService filterService;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testReceiveMessage() throws JsonProcessingException {
    final Map<String, Object> message = Map.of(
            "int_field", 15,
            "string_filed", "test",
            "bool_field", true
    );
    kafkaTemplate.send("test_filter", objectMapper.writeValueAsString(message));
    kafkaTemplate.flush();

    await().atMost(Duration.ofSeconds(5))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(filterService).processMessage(eq(message)));
  }
}
