package org.hsse.highloadstreamprocessor.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = DatabaseSuit.Initializer.class)
public class DatabaseSuit {
  protected static final KafkaContainer KAFKA =
          new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"));

  public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(@NotNull ConfigurableApplicationContext context) {
      Startables.deepStart(KAFKA).join();

      TestPropertyValues.of(
              "spring.kafka.bootstrap-servers=" + KAFKA.getBootstrapServers()
      ).applyTo(context);
    }
  }
}
