package org.hsse.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DeduplicationRequestsDto {
  private List<String> columns;

  public DeduplicationRequestsDto() {}

  public DeduplicationRequestsDto(List<String> columns) {
    this.columns = columns;
  }
}