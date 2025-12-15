package org.hsse.service.filter;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class JsonFilterRequest {
  private String userId;
  private List<JsonFilterCondition> filters;
  private List<String> selectFields;
  private Integer limit = 50;
  private Integer offset = 0;
}
