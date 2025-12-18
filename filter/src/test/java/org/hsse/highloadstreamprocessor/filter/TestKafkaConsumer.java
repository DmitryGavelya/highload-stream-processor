package org.hsse.highloadstreamprocessor.filter;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

class TestKafkaConsumer implements AutoCloseable {
  private final KafkaConsumer<String, String> consumer;

  public TestKafkaConsumer(String bootstrapServers, String groupId) {
    Properties props = new Properties();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    this.consumer = new KafkaConsumer<>(props);
  }

  public void subscribe(List<String> topics) {
    consumer.subscribe(topics);
  }

  public ConsumerRecords<String, String> poll() {
    return consumer.poll(Duration.ofSeconds(5));
  }

  @Override
  public void close() {
    consumer.close(Duration.ofSeconds(5));
  }
}
