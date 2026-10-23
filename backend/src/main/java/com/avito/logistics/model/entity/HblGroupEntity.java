package com.avito.logistics.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hbl_groups")
public class HblGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    @Column(name = "group_id", nullable = false, length = 64)
    private String groupId;

    @Lob
    @Column(name = "document_ids_json", columnDefinition = "TEXT", nullable = false)
    private String documentIdsJson;

    @Column(name = "same_shipper_and_consignee", nullable = false)
    private boolean sameShipperAndConsignee;

    @Column(name = "hbl_action", nullable = false, length = 64)
    private String hblAction;

    @Column(name = "reason", length = 512)
    private String reason;

    @Lob
    @Column(name = "combined_extraction_json", columnDefinition = "TEXT", nullable = false)
    private String combinedExtractionJson;

    public HblGroupEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getDocumentIdsJson() { return documentIdsJson; }
    public void setDocumentIdsJson(String documentIdsJson) { this.documentIdsJson = documentIdsJson; }

    public boolean isSameShipperAndConsignee() { return sameShipperAndConsignee; }
    public void setSameShipperAndConsignee(boolean sameShipperAndConsignee) { this.sameShipperAndConsignee = sameShipperAndConsignee; }

    public String getHblAction() { return hblAction; }
    public void setHblAction(String hblAction) { this.hblAction = hblAction; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCombinedExtractionJson() { return combinedExtractionJson; }
    public void setCombinedExtractionJson(String combinedExtractionJson) { this.combinedExtractionJson = combinedExtractionJson; }
}
