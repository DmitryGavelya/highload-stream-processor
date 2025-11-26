package org.hsse.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DeduplicationRequestDto {
  private String userId;
  private List<String> columns;

  public DeduplicationRequestDto() {}

  public DeduplicationRequestDto(String userId, List<String> columns) {
    this.userId = userId;
    this.columns = columns;
  }
}
