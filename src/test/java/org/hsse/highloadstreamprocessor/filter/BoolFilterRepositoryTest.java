package org.hsse.highloadstreamprocessor.filter;

import org.hsse.highloadstreamprocessor.filter.db.BoolFilterEntity;
import org.hsse.highloadstreamprocessor.filter.db.BoolFilterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

// this test is an example and a check for config working properly
// TODO: remove it from production ready code
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BoolFilterRepositoryTest extends DatabaseSuit {
  @Autowired
  BoolFilterRepository repository;

  @Test
  @Transactional
  void testFindAll() {
    BoolFilterEntity filter1 = new BoolFilterEntity("field1", true);
    repository.save(filter1);

    BoolFilterEntity filter2 = new BoolFilterEntity("field2", true);
    repository.save(filter2);

    assertEquals(List.of(filter1, filter2), repository.findAll());
  }

  @Test
  @Transactional
  void testFindById() {
    BoolFilterEntity filter1 = new BoolFilterEntity("field1", true);
    repository.save(filter1);

    BoolFilterEntity filter2 = new BoolFilterEntity("field2", true);
    repository.save(filter2);

    assertEquals(Optional.of(filter1), repository.findById(filter1.getFieldName()));
  }
}
