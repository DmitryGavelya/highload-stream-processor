package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;

public record AnyFilter(Filter nested) implements Filter {
  @Override
  public boolean accept(JsonNode message) {
    if (!message.isArray()) {
      return false;
    }

    return message.valueStream().anyMatch(nested::accept);
  }
}
