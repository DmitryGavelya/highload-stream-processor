package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AndFilter(List<Filter> nested) implements Filter {
  @Override
  public boolean accept(@NotNull JsonNode message) {
    return nested.stream().allMatch(filter -> filter.accept(message));
  }
}
