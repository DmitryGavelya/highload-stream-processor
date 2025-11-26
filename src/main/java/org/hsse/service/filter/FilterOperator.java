package org.hsse.service.filter;

public enum FilterOperator {
  EQUALS, NOT_EQUALS,
  GREATER_THAN, LESS_THAN,
  GREATER_OR_EQUAL, LESS_OR_EQUAL,
  CONTAINS, STARTS_WITH, ENDS_WITH,
  IN, NOT_IN,
  EXISTS, NOT_EXISTS
}
