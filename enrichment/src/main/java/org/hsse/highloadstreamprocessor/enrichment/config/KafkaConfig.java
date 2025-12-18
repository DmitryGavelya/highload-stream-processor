package org.hsse.highloadstreamprocessor.enrichment.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${enrichment.listen-to}")
    private String listenToTopicName;

    @Value("${enrichment.send-to}")
    private String sendToTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic listenToTopic() {
        return new NewTopic(listenToTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic sendToTopic() {
        return new NewTopic(sendToTopicName, 1, (short) 1);
    }
}
