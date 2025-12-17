package org.hsse.service.enrichment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hsse.entity.UserEnrichment;
import org.hsse.repository.UserEnrichmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrichmentService {

  private final UserEnrichmentRepository enrichmentRepository;

  @Value("${enrichment.host}")
  private String enrichmentHost;

  @Value("${enrichment.port}")
  private String enrichmentPort;

  @Value("${enrichment.api-trace}")
  private String enrichmentApiTrace;

  @Transactional
  public void addEnrichmentColumn(String userId, String columnName) {
    if (!enrichmentRepository.existsByUserIdAndColumnName(userId, columnName)) {
      UserEnrichment column = new UserEnrichment();
      column.setUserId(userId);
      column.setColumnName(columnName);
      enrichmentRepository.save(column);
    }
  }

  @Transactional
  public void addEnrichmentColumns(String userId, List<String> columns) {
    for (String columnName : columns) {
      if (!enrichmentRepository.existsByUserIdAndColumnName(userId, columnName)) {
        UserEnrichment column = new UserEnrichment();
        column.setUserId(userId);
        column.setColumnName(columnName);
        enrichmentRepository.save(column);
      }
    }
  }

  @Transactional
  public void removeEnrichmentColumn(String userId, String columnName) {
    enrichmentRepository.deleteByUserIdAndColumnName(userId, columnName);
  }

  public List<String> getEnrichmentColumns(String userId) {
    return enrichmentRepository.findColumnNamesByUserId(userId);
  }

  @Transactional
  public void clearEnrichmentColumns(String userId) {
    enrichmentRepository.deleteByUserId(userId);
  }

  public boolean hasEnrichmentColumn(String userId, String columnName) {
    return enrichmentRepository.existsByUserIdAndColumnName(userId, columnName);
  }

  @Transactional
    public boolean getFromEnrichmentService(String originId) throws JsonProcessingException {
      RestTemplate restTemplate = new RestTemplate();
      String url = String.format("http://%s:%s/%s/%s", enrichmentHost, enrichmentPort, enrichmentApiTrace, originId);

      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
          ObjectMapper objectMapper = new ObjectMapper();
          var resultMap = objectMapper.readValue(response.getBody(), Map.class);
          addEnrichmentColumn(originId, resultMap.get("query_value").toString());
          return true;
      }

      return false;
  }
}
