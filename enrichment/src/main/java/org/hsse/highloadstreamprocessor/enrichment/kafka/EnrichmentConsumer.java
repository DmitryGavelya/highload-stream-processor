package org.hsse.highloadstreamprocessor.enrichment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.hsse.highloadstreamprocessor.enrichment.service.EnrichmentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class EnrichmentConsumer {
    private final EnrichmentService enrichmentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"${enrichment.listen-to}"})
    public void consumeMessage(String message) throws JsonProcessingException {
        HashMap<String, Object> parsedMessage = objectMapper
                .readValue(message, new TypeReference<>() {});
        enrichmentService.processMessage(parsedMessage);
    }
}
