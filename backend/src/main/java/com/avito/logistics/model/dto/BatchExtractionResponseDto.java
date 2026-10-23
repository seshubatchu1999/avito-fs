package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchExtractionResponseDto {

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("documents")
    private List<ExtractedPackingListDto> documents = new ArrayList<>();

    @JsonProperty("hbl_groups")
    private List<HblGroupDto> hblGroups = new ArrayList<>();

    public BatchExtractionResponseDto() {}

    public BatchExtractionResponseDto(String batchId, List<ExtractedPackingListDto> documents, List<HblGroupDto> hblGroups) {
        this.batchId = batchId;
        this.documents = documents != null ? documents : new ArrayList<>();
        this.hblGroups = hblGroups != null ? hblGroups : new ArrayList<>();
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public List<ExtractedPackingListDto> getDocuments() { return documents; }
    public void setDocuments(List<ExtractedPackingListDto> documents) { this.documents = documents; }

    public List<HblGroupDto> getHblGroups() { return hblGroups; }
    public void setHblGroups(List<HblGroupDto> hblGroups) { this.hblGroups = hblGroups; }
}
