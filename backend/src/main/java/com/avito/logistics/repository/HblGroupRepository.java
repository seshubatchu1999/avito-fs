package com.avito.logistics.repository;

import com.avito.logistics.model.entity.HblGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HblGroupRepository extends JpaRepository<HblGroupEntity, Long> {
    List<HblGroupEntity> findByBatchIdOrderByIdAsc(String batchId);
    Optional<HblGroupEntity> findByBatchIdAndGroupId(String batchId, String groupId);
    void deleteByBatchId(String batchId);
}
