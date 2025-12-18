package org.hsse.highloadstreamprocessor.deduplication.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    final Map<String, Object> config = new HashMap<>();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new KafkaAdmin(config);
  }

  @Bean
  public NewTopic filteredTopic() {
    return new NewTopic("filtered", 1, (short) 1);
  }

  @Bean
  public NewTopic deduplicatedTopic() {
    return new NewTopic("deduplicated", 1, (short) 1);
  }
}

