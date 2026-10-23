package com.avito.logistics.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mbl_records")
public class MblRecordEntity {

    @Id
    @Column(name = "batch_id", nullable = false, unique = true, length = 64)
    private String batchId;

    @Column(name = "mbl_number", nullable = false, length = 128)
    private String mblNumber;

    @Lob
    @Column(name = "manual_details_json", columnDefinition = "TEXT", nullable = false)
    private String manualDetailsJson;

    public MblRecordEntity() {}

    public MblRecordEntity(String batchId, String mblNumber, String manualDetailsJson) {
        this.batchId = batchId;
        this.mblNumber = mblNumber;
        this.manualDetailsJson = manualDetailsJson;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getMblNumber() { return mblNumber; }
    public void setMblNumber(String mblNumber) { this.mblNumber = mblNumber; }

    public String getManualDetailsJson() { return manualDetailsJson; }
    public void setManualDetailsJson(String manualDetailsJson) { this.manualDetailsJson = manualDetailsJson; }
}
