package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HblManualDetailsDto {

    @JsonProperty("hbl_number")
    private String hblNumber;

    @JsonProperty("notify_party")
    private PartyDto notifyParty = new PartyDto();

    @JsonProperty("container_number")
    private String containerNumber;

    @JsonProperty("seal_number")
    private String sealNumber;

    @JsonProperty("freight_terms")
    private String freightTerms;

    public HblManualDetailsDto() {}

    public String getHblNumber() { return hblNumber; }
    public void setHblNumber(String hblNumber) { this.hblNumber = hblNumber; }

    public PartyDto getNotifyParty() { return notifyParty; }
    public void setNotifyParty(PartyDto notifyParty) { this.notifyParty = notifyParty != null ? notifyParty : new PartyDto(); }

    public String getContainerNumber() { return containerNumber; }
    public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }

    public String getSealNumber() { return sealNumber; }
    public void setSealNumber(String sealNumber) { this.sealNumber = sealNumber; }

    public String getFreightTerms() { return freightTerms; }
    public void setFreightTerms(String freightTerms) { this.freightTerms = freightTerms; }
}
