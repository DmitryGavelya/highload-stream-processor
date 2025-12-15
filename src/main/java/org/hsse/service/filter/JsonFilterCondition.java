package org.hsse.service.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class JsonFilterCondition {
  private String field;
  private FilterOperator operator;
  private Object value;
  private ValueType valueType;
}
