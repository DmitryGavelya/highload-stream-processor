package org.hsse.highloadstreamprocessor.filter.dsl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotFilter.class, name = "not"),
        @JsonSubTypes.Type(value = AndFilter.class, name = "and"),
        @JsonSubTypes.Type(value = OrFilter.class, name = "or"),
        @JsonSubTypes.Type(value = EqualFilter.class, name = "="),
        @JsonSubTypes.Type(value = WithFieldFilter.class, name = "with"),
        @JsonSubTypes.Type(value = AllFilter.class, name = "all"),
        @JsonSubTypes.Type(value = AnyFilter.class, name = "any"),
        @JsonSubTypes.Type(value = InFilter.class, name = "in"),
})
public sealed interface Filter
        permits NotFilter, AndFilter, OrFilter, EqualFilter,
        WithFieldFilter, AllFilter, AnyFilter, InFilter {
  boolean accept(JsonNode message);
}
