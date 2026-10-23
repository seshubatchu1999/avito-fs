package com.avito.logistics.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "batches")
public class BatchEntity {

    @Id
    @Column(name = "batch_id", nullable = false, unique = true, length = 64)
    private String batchId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public BatchEntity() {}

    public BatchEntity(String batchId) {
        this.batchId = batchId;
        this.createdAt = Instant.now();
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
