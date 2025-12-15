package org.hsse.dto;

import lombok.Getter;
import lombok.Setter;
import org.hsse.service.filter.FilterOperator;

@Getter
@Setter
public class UserFilterDto {
  private String field;
  private FilterOperator operator;
  private String value;

  public UserFilterDto() {}

  public UserFilterDto(String field, FilterOperator operator, String value) {
    this.field = field;
    this.operator = operator;
    this.value = value;
  }
}
