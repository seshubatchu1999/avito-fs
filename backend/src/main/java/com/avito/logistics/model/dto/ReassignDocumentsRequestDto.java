package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class ReassignDocumentsRequestDto {

    @NotBlank
    @JsonProperty("batch_id")
    private String batchId;

    @NotBlank
    @JsonProperty("source_group_id")
    private String sourceGroupId;

    @NotBlank
    @JsonProperty("target_group_id")
    private String targetGroupId;

    @NotEmpty
    @JsonProperty("added_document_ids")
    private List<String> addedDocumentIds = new ArrayList<>();

    @JsonProperty("create_target_group")
    private boolean createTargetGroup = false;

    public ReassignDocumentsRequestDto() {}

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getSourceGroupId() { return sourceGroupId; }
    public void setSourceGroupId(String sourceGroupId) { this.sourceGroupId = sourceGroupId; }

    public String getTargetGroupId() { return targetGroupId; }
    public void setTargetGroupId(String targetGroupId) { this.targetGroupId = targetGroupId; }

    public List<String> getAddedDocumentIds() { return addedDocumentIds; }
    public void setAddedDocumentIds(List<String> addedDocumentIds) { this.addedDocumentIds = addedDocumentIds; }

    public boolean isCreateTargetGroup() { return createTargetGroup; }
    public void setCreateTargetGroup(boolean createTargetGroup) { this.createTargetGroup = createTargetGroup; }
}
