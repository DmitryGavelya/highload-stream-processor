package org.hsse.highloadstreamprocessor.filter;

import org.hsse.highloadstreamprocessor.filter.db.BoolFilterEntity;
import org.hsse.highloadstreamprocessor.filter.db.BoolFilterRepository;
import org.hsse.highloadstreamprocessor.filter.db.IntFilterEntity;
import org.hsse.highloadstreamprocessor.filter.db.IntFilterRepository;
import org.hsse.highloadstreamprocessor.filter.db.StringFilterEntity;
import org.hsse.highloadstreamprocessor.filter.db.StringFilterRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FilterService.class)
@ActiveProfiles("test")
public class FilterServiceTest {
  @Autowired
  private FilterService filterService;

  @MockitoBean
  private IntFilterRepository intFilterRepository;

  @MockitoBean
  private StringFilterRepository stringFilterRepository;

  @MockitoBean
  private BoolFilterRepository boolFilterRepository;

  @MockitoBean
  private FilteredSender sender;

  @Test
  void testMatching() {
    when(intFilterRepository.findAll())
            .thenReturn(List.of(new IntFilterEntity("int_field", 15)));
    when(stringFilterRepository.findAll())
            .thenReturn(List.of(new StringFilterEntity("string_field", "test")));
    when(boolFilterRepository.findAll())
            .thenReturn(List.of(new BoolFilterEntity("bool_field", true)));

    final Map<String, Object> matching = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", true
    );

    filterService.processMessage(matching);

    Mockito.verify(sender).sendMessage(matching);
  }

  @Test
  void testExtraField() {
    when(intFilterRepository.findAll())
            .thenReturn(List.of(new IntFilterEntity("int_field", 15)));
    when(stringFilterRepository.findAll())
            .thenReturn(List.of(new StringFilterEntity("string_field", "test")));
    when(boolFilterRepository.findAll())
            .thenReturn(List.of(new BoolFilterEntity("bool_field", true)));

    final Map<String, Object> matching = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", true,
            "extra_field", "blah-blah-blah"
    );

    filterService.processMessage(matching);

    Mockito.verify(sender).sendMessage(matching);
  }

  @Test
  void testValueNotMatching() {
    when(intFilterRepository.findAll())
            .thenReturn(List.of(new IntFilterEntity("int_field", 15)));
    when(stringFilterRepository.findAll())
            .thenReturn(List.of(new StringFilterEntity("string_field", "test")));
    when(boolFilterRepository.findAll())
            .thenReturn(List.of(new BoolFilterEntity("bool_field", true)));

    Map<String, Object> notMatching = Map.of(
            "int_field", 15,
            "string_field", "value_mismatch",
            "bool_field", true
    );

    filterService.processMessage(notMatching);

    Mockito.verify(sender, never()).sendMessage(anyMap());
  }

  @Test
  void testTypeNotMatching() {
    when(intFilterRepository.findAll())
            .thenReturn(List.of(new IntFilterEntity("int_field", 15)));
    when(stringFilterRepository.findAll())
            .thenReturn(List.of(new StringFilterEntity("string_field", "test")));
    when(boolFilterRepository.findAll())
            .thenReturn(List.of(new BoolFilterEntity("bool_field", true)));

    Map<String, Object> notMatching = Map.of(
            "int_field", 15,
            "string_field", "test",
            "bool_field", "type_mismatch"
    );

    filterService.processMessage(notMatching);

    Mockito.verify(sender, never()).sendMessage(anyMap());
  }

  @Test
  void testNameNotMatching() {
    when(intFilterRepository.findAll())
            .thenReturn(List.of(new IntFilterEntity("int_field", 15)));
    when(stringFilterRepository.findAll())
            .thenReturn(List.of(new StringFilterEntity("string_field", "test")));
    when(boolFilterRepository.findAll())
            .thenReturn(List.of(new BoolFilterEntity("bool_field", true)));

    Map<String, Object> notMatching = Map.of(
            "name_mismatch", 15,
            "string_field", "test",
            "bool_field", true
    );

    filterService.processMessage(notMatching);

    Mockito.verify(sender, never()).sendMessage(anyMap());
  }
}
