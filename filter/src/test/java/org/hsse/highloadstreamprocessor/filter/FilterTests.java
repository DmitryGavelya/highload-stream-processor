package org.hsse.highloadstreamprocessor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hsse.highloadstreamprocessor.filter.dsl.Filter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Some tests big enough to be reasonable but still about filters and their parsing
public class FilterTests {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void andEqualParseAndAccept() throws JsonProcessingException {
    final Filter filter = objectMapper.readValue("""
            {
              "type": "and",
              "nested": [
                { "type": "=", "field": "int_field", "value": 15 },
                { "type": "=", "field": "string_field", "value": "test" },
                { "type": "=", "field": "bool_field", "value": true }
              ]
            }
            """, Filter.class);

    final JsonNode message = objectMapper.readTree("""
              {
                "int_field": 15,
                "string_field": "test",
                "bool_field": true
              }
            """);

    assertTrue(filter.accept(message));
  }

  @Test
  void notWithAnyInParseAndAccept() throws JsonProcessingException {
    final Filter filter = objectMapper.readValue("""
            {
              "type": "not",
              "nested": {
                "type": "with",
                "field": "tags",
                "nested": {
                  "type": "any",
                  "nested": {
                    "type": "in",
                    "values": ["banned1", "banned2"]
                  }
                }
              }
            }
            """, Filter.class);

    final JsonNode message = objectMapper.readTree("""
              {
                "tags": ["ok", "ok2"],
                "other_data": "blah blah blah"
              }
            """);

    assertTrue(filter.accept(message));
  }

  @Test
  void notWithAnyInParseAndNotAccept() throws JsonProcessingException {
    final Filter filter = objectMapper.readValue("""
            {
              "type": "not",
              "nested": {
                "type": "with",
                "nested": {
                  "type": "any",
                  "nested": {
                    "type": "in",
                    "values": ["banned1", "banned2"]
                  }
                }
              }
            }
            """, Filter.class);

    final JsonNode message = objectMapper.readTree("""
              {
                "tags": ["ok", "banned2"],
                "other_data": "blah blah blah"
              }
            """);

    assertTrue(filter.accept(message));
  }
}
