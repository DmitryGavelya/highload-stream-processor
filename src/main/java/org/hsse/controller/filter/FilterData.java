package org.hsse.controller.filter;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Builder
@Getter
public class FilterData {
  private String userId;
  private String select;
  private String from;
  private String where;
  private String groupBy;
  private String having;
  private String orderBy;

  public FilterData() {}

}
