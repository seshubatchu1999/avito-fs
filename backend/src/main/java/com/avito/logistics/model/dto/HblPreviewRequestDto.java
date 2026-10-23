package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class HblPreviewRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    @NotBlank
    @JsonProperty("group_id")
    private String groupId;

    public HblPreviewRequestDto() {}

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}
