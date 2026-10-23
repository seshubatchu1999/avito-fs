package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class ReassignDocumentsResponseDto {

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("hbl_groups")
    private List<HblGroupDto> hblGroups = new ArrayList<>();

    public ReassignDocumentsResponseDto() {}

    public ReassignDocumentsResponseDto(String batchId, List<HblGroupDto> hblGroups) {
        this.batchId = batchId;
        this.hblGroups = hblGroups != null ? hblGroups : new ArrayList<>();
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public List<HblGroupDto> getHblGroups() { return hblGroups; }
    public void setHblGroups(List<HblGroupDto> hblGroups) { this.hblGroups = hblGroups; }
}
