package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HblGenerationRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    @NotBlank
    @JsonProperty("group_id")
    private String groupId;

    @NotNull
    @JsonProperty("manual_details")
    private HblManualDetailsDto manualDetails;

    public HblGenerationRequestDto() {}

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public HblManualDetailsDto getManualDetails() { return manualDetails; }
    public void setManualDetails(HblManualDetailsDto manualDetails) { this.manualDetails = manualDetails; }
}
