package com.avito.logistics.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hbl_records")
public class HblRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    @Column(name = "group_id", nullable = false, length = 64)
    private String groupId;

    @Column(name = "hbl_number", nullable = false, length = 128)
    private String hblNumber;

    @Lob
    @Column(name = "manual_details_json", columnDefinition = "TEXT", nullable = false)
    private String manualDetailsJson;

    public HblRecordEntity() {}

    public HblRecordEntity(String batchId, String groupId, String hblNumber, String manualDetailsJson) {
        this.batchId = batchId;
        this.groupId = groupId;
        this.hblNumber = hblNumber;
        this.manualDetailsJson = manualDetailsJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getHblNumber() { return hblNumber; }
    public void setHblNumber(String hblNumber) { this.hblNumber = hblNumber; }

    public String getManualDetailsJson() { return manualDetailsJson; }
    public void setManualDetailsJson(String manualDetailsJson) { this.manualDetailsJson = manualDetailsJson; }
}
