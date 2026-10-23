package com.avito.logistics.repository;

import com.avito.logistics.model.entity.MblRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MblRecordRepository extends JpaRepository<MblRecordEntity, String> {
    Optional<MblRecordEntity> findByBatchId(String batchId);
}
