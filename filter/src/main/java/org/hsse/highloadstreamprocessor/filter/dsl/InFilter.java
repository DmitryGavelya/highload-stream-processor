package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record InFilter(String field, List<JsonNode> values) implements Filter {
  @Override
  public boolean accept(JsonNode message) {
    final JsonNode fieldValue = (field == null ? message : message.get(field));
    if (fieldValue == null) {
      return false;
    }
    return values.contains(fieldValue);
  }
}
