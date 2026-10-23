package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class MblPreviewRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("manual_details")
    private MblManualDetailsDto manualDetails = new MblManualDetailsDto();

    public MblPreviewRequestDto() {}

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public MblManualDetailsDto getManualDetails() { return manualDetails; }
    public void setManualDetails(MblManualDetailsDto manualDetails) { this.manualDetails = manualDetails != null ? manualDetails : new MblManualDetailsDto(); }
}
