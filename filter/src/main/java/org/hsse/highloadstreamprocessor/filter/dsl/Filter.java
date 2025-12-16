package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AndFilter.class, name = "and"),
        @JsonSubTypes.Type(value = OrFilter.class, name = "or"),
        @JsonSubTypes.Type(value = EqualFilter.class, name = "="),
})
public sealed interface Filter permits AndFilter, OrFilter, EqualFilter {
  boolean accept(JsonNode message);
}
