package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;

public record NotFilter(Filter nested) implements Filter {
  @Override
  public boolean accept(JsonNode message) {
    return !nested.accept(message);
  }
}
