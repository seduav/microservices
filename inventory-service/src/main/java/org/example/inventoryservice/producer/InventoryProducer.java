package org.example.inventoryservice.producer;

import lombok.RequiredArgsConstructor;
import org.example.events.InventoryResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, InventoryResult> kafkaTemplate;

    public void publish(InventoryResult result) {
        kafkaTemplate.send("inventory-result", result);
    }
}