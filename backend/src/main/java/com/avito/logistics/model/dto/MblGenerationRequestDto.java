package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MblGenerationRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    @NotNull
    @JsonProperty("manual_details")
    private MblManualDetailsDto manualDetails;

    public MblGenerationRequestDto() {}

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public MblManualDetailsDto getManualDetails() { return manualDetails; }
    public void setManualDetails(MblManualDetailsDto manualDetails) { this.manualDetails = manualDetails; }
}
