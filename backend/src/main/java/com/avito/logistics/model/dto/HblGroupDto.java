package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HblGroupDto {

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("document_ids")
    private List<String> documentIds = new ArrayList<>();

    @JsonProperty("same_shipper_and_consignee")
    private boolean sameShipperAndConsignee;

    @JsonProperty("hbl_action")
    private String hblAction; // "create_combined_hbl" or "create_separate_hbl"

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("combined_extraction")
    private PackingListDto combinedExtraction;

    public HblGroupDto() {}

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public List<String> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<String> documentIds) { this.documentIds = documentIds != null ? documentIds : new ArrayList<>(); }

    public boolean isSameShipperAndConsignee() { return sameShipperAndConsignee; }
    public void setSameShipperAndConsignee(boolean sameShipperAndConsignee) { this.sameShipperAndConsignee = sameShipperAndConsignee; }

    public String getHblAction() { return hblAction; }
    public void setHblAction(String hblAction) { this.hblAction = hblAction; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public PackingListDto getCombinedExtraction() { return combinedExtraction; }
    public void setCombinedExtraction(PackingListDto combinedExtraction) { this.combinedExtraction = combinedExtraction; }
}
