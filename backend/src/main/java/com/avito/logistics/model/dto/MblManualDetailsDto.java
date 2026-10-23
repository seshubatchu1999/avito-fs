package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MblManualDetailsDto {

    @JsonProperty("mbl_number")
    private String mblNumber;

    @JsonProperty("pre_carriage_by")
    private String preCarriageBy;

    @JsonProperty("place_of_receipt")
    private String placeOfReceipt;

    @JsonProperty("freight_terms")
    private String freightTerms;

    @JsonProperty("number_of_originals")
    private String numberOfOriginals;

    @JsonProperty("vessel_name")
    private String vesselName;

    @JsonProperty("voyage_number")
    private String voyageNumber;

    @JsonProperty("port_of_loading")
    private String portOfLoading;

    @JsonProperty("port_of_discharge")
    private String portOfDischarge;

    @JsonProperty("place_of_final_delivery")
    private String placeOfFinalDelivery;

    @JsonProperty("tare_weight")
    private String tareWeight;

    @JsonProperty("verified_gross_mass")
    private String verifiedGrossMass;

    @JsonProperty("carrier_booking_reference")
    private String carrierBookingReference;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("shipper")
    private PartyDto shipper = new PartyDto();

    @JsonProperty("consignee")
    private PartyDto consignee = new PartyDto();

    @JsonProperty("cargo_description")
    private String cargoDescription;

    @JsonProperty("total_packages")
    private String totalPackages;

    @JsonProperty("total_net_weight")
    private String totalNetWeight;

    @JsonProperty("total_gross_weight")
    private String totalGrossWeight;

    @JsonProperty("total_measurement")
    private String totalMeasurement;

    public MblManualDetailsDto() {}

    // Getters and Setters
    public String getMblNumber() { return mblNumber; }
    public void setMblNumber(String mblNumber) { this.mblNumber = mblNumber; }

    public String getPreCarriageBy() { return preCarriageBy; }
    public void setPreCarriageBy(String preCarriageBy) { this.preCarriageBy = preCarriageBy; }

    public String getPlaceOfReceipt() { return placeOfReceipt; }
    public void setPlaceOfReceipt(String placeOfReceipt) { this.placeOfReceipt = placeOfReceipt; }

    public String getFreightTerms() { return freightTerms; }
    public void setFreightTerms(String freightTerms) { this.freightTerms = freightTerms; }

    public String getNumberOfOriginals() { return numberOfOriginals; }
    public void setNumberOfOriginals(String numberOfOriginals) { this.numberOfOriginals = numberOfOriginals; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVoyageNumber() { return voyageNumber; }
    public void setVoyageNumber(String voyageNumber) { this.voyageNumber = voyageNumber; }

    public String getPortOfLoading() { return portOfLoading; }
    public void setPortOfLoading(String portOfLoading) { this.portOfLoading = portOfLoading; }

    public String getPortOfDischarge() { return portOfDischarge; }
    public void setPortOfDischarge(String portOfDischarge) { this.portOfDischarge = portOfDischarge; }

    public String getPlaceOfFinalDelivery() { return placeOfFinalDelivery; }
    public void setPlaceOfFinalDelivery(String placeOfFinalDelivery) { this.placeOfFinalDelivery = placeOfFinalDelivery; }

    public String getTareWeight() { return tareWeight; }
    public void setTareWeight(String tareWeight) { this.tareWeight = tareWeight; }

    public String getVerifiedGrossMass() { return verifiedGrossMass; }
    public void setVerifiedGrossMass(String verifiedGrossMass) { this.verifiedGrossMass = verifiedGrossMass; }

    public String getCarrierBookingReference() { return carrierBookingReference; }
    public void setCarrierBookingReference(String carrierBookingReference) { this.carrierBookingReference = carrierBookingReference; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public PartyDto getShipper() { return shipper; }
    public void setShipper(PartyDto shipper) { this.shipper = shipper != null ? shipper : new PartyDto(); }

    public PartyDto getConsignee() { return consignee; }
    public void setConsignee(PartyDto consignee) { this.consignee = consignee != null ? consignee : new PartyDto(); }

    public String getCargoDescription() { return cargoDescription; }
    public void setCargoDescription(String cargoDescription) { this.cargoDescription = cargoDescription; }

    public String getTotalPackages() { return totalPackages; }
    public void setTotalPackages(String totalPackages) { this.totalPackages = totalPackages; }

    public String getTotalNetWeight() { return totalNetWeight; }
    public void setTotalNetWeight(String totalNetWeight) { this.totalNetWeight = totalNetWeight; }

    public String getTotalGrossWeight() { return totalGrossWeight; }
    public void setTotalGrossWeight(String totalGrossWeight) { this.totalGrossWeight = totalGrossWeight; }

    public String getTotalMeasurement() { return totalMeasurement; }
    public void setTotalMeasurement(String totalMeasurement) { this.totalMeasurement = totalMeasurement; }
}
