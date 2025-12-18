package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;

public record WithFieldFilter(String field, Filter nested) implements Filter {
  @Override
  public boolean accept(JsonNode message) {
    final JsonNode value = message.get(field);
    if (value == null) {
      return false;
    }
    return nested.accept(value);
  }
}
