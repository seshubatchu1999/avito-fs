package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class InvoiceSheetRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    public InvoiceSheetRequestDto() {}

    public InvoiceSheetRequestDto(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
}
