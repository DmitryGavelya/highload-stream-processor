package org.hsse.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class EnrichmentConfigDto {
  private String userId;
  private List<String> columns;

  public EnrichmentConfigDto() {}

  public EnrichmentConfigDto(String userId, List<String> columns) {
    this.userId = userId;
    this.columns = columns;
  }
}
