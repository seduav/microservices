package org.example.inventoryservice.repository;

import org.example.inventoryservice.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository
        extends JpaRepository<ProcessedMessage, String> {
    boolean existsByMessageId(String messageId);
}