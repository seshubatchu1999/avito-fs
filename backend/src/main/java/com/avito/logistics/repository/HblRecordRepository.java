package com.avito.logistics.repository;

import com.avito.logistics.model.entity.HblRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HblRecordRepository extends JpaRepository<HblRecordEntity, Long> {
    List<HblRecordEntity> findByBatchId(String batchId);
    Optional<HblRecordEntity> findByBatchIdAndGroupId(String batchId, String groupId);
    Optional<HblRecordEntity> findByBatchIdAndHblNumber(String batchId, String hblNumber);
}
