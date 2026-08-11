package org.example.orderservice.config;

import org.example.events.InventoryResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, InventoryResult> inventoryConsumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"inventory-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        JsonDeserializer<InventoryResult> deserializer =
                new JsonDeserializer<>(InventoryResult.class);

        deserializer.addTrustedPackages("org.example.events");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryResult>
    inventoryKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InventoryResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryConsumerFactory());

        return factory;
    }
}