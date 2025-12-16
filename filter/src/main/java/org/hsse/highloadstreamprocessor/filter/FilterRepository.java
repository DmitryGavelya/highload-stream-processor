package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.hsse.highloadstreamprocessor.filter.dsl.Filter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FilterRepository {
  private final JdbcTemplate jdbcTemplate;
  private final ObjectMapper objectMapper;

  public Filter loadFilter() throws JsonProcessingException {
    final String sql = "SELECT * FROM filters";
    final String json = jdbcTemplate.queryForObject(sql, String.class);
    return objectMapper.readValue(json, Filter.class);
  }
}
