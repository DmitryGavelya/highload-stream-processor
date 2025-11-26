package org.hsse.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class FilterController {

  @PostMapping("/filter")
  public void AddFilterConfig(@RequestBody FilterData filterData) {

  }
}
