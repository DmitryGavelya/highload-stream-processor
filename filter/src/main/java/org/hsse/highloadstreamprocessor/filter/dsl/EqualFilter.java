package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

public record EqualFilter(String field, JsonNode value) implements Filter {
  public EqualFilter(JsonNode value) {
    this("", value);
  }

  @Override
  public boolean accept(@NotNull JsonNode message) {
    final JsonNode toCompare = (field.isEmpty() ? message : message.get(field));
    if (toCompare == null) {
      return false;
    }

    return toCompare.equals(value);
  }
}
