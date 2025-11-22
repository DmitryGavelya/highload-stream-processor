package org.hsse.highloadstreamprocessor.enrichment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EnrichmentSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String enrichmentedTopic;


    public EnrichmentSender(KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper objectMapper,
                            @Value("${enrichment.send-to}") String enrichmentedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.enrichmentedTopic = enrichmentedTopic;
    }

    public void sendMessage(Map<String, Object> message) {
        try {
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(enrichmentedTopic, objectMapper.writeValueAsString(message));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
