package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackingListDto {

    @JsonProperty("packing_list_number")
    private String packingListNumber;

    @JsonProperty("packing_list_date")
    private String packingListDate;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("invoice_date")
    private String invoiceDate;

    @JsonProperty("consignee_gst_number")
    private String consigneeGstNumber;

    @JsonProperty("exporter_gst_number")
    private String exporterGstNumber;

    @JsonProperty("cgst_percentage")
    private String cgstPercentage;

    @JsonProperty("cgst_amount")
    private String cgstAmount;

    @JsonProperty("sgst_percentage")
    private String sgstPercentage;

    @JsonProperty("sgst_amount")
    private String sgstAmount;

    @JsonProperty("vehicle_number")
    private String vehicleNumber;

    @JsonProperty("shipping_from")
    private String shippingFrom;

    @JsonProperty("shipping_to")
    private String shippingTo;

    @JsonProperty("reference_number")
    private String referenceNumber;

    @JsonProperty("exporter_reference")
    private String exporterReference;

    @JsonProperty("ie_code")
    private String ieCode;

    @JsonProperty("branch_code")
    private String branchCode;

    @JsonProperty("buyer_order_number")
    private String buyerOrderNumber;

    @JsonProperty("buyer_order_date")
    private String buyerOrderDate;

    @JsonProperty("main_item_description")
    private String mainItemDescription;

    @JsonProperty("other_references")
    private List<String> otherReferences = new ArrayList<>();

    @JsonProperty("shipper_exporter")
    private PartyDto shipperExporter;

    @JsonProperty("vendor")
    private PartyDto vendor;

    @JsonProperty("consignee")
    private PartyDto consignee;

    @JsonProperty("buyer")
    private PartyDto buyer;

    @JsonProperty("notify_party")
    private PartyDto notifyParty;

    @JsonProperty("country_of_origin")
    private String countryOfOrigin;

    @JsonProperty("country_of_final_destination")
    private String countryOfFinalDestination;

    @JsonProperty("pre_carriage_by")
    private String preCarriageBy;

    @JsonProperty("place_of_receipt")
    private String placeOfReceipt;

    @JsonProperty("vessel_name")
    private String vesselName;

    @JsonProperty("voyage_or_flight_number")
    private String voyageOrFlightNumber;

    @JsonProperty("port_of_loading")
    private String portOfLoading;

    @JsonProperty("route_or_place_of_transshipment")
    private String routeOrPlaceOfTransshipment;

    @JsonProperty("port_of_discharge")
    private String portOfDischarge;

    @JsonProperty("place_of_final_delivery")
    private String placeOfFinalDelivery;

    @JsonProperty("type_of_movement")
    private String typeOfMovement;

    @JsonProperty("incoterms")
    private String incoterms;

    @JsonProperty("payment_terms")
    private String paymentTerms;

    @JsonProperty("freight_terms")
    private String freightTerms;

    @JsonProperty("freight_amount")
    private String freightAmount;

    @JsonProperty("release_terms")
    private String releaseTerms;

    @JsonProperty("shipment_reference")
    private String shipmentReference;

    @JsonProperty("place_of_issue")
    private String placeOfIssue;

    @JsonProperty("date_of_issue")
    private String dateOfIssue;

    @JsonProperty("shipped_on_board_date")
    private String shippedOnBoardDate;

    @JsonProperty("number_of_originals")
    private String numberOfOriginals;

    @JsonProperty("delivery_contact")
    private String deliveryContact;

    @JsonProperty("other_particulars")
    private String otherParticulars;

    @JsonProperty("marks_and_numbers")
    private String marksAndNumbers;

    @JsonProperty("total_package_count")
    private String totalPackageCount;

    @JsonProperty("total_package_type")
    private String totalPackageType;

    @JsonProperty("total_gross_weight")
    private String totalGrossWeight;

    @JsonProperty("total_net_weight")
    private String totalNetWeight;

    @JsonProperty("total_measurement")
    private String totalMeasurement;

    @JsonProperty("total_amount")
    private String totalAmount;

    @JsonProperty("items")
    private List<PackingListItemDto> items = new ArrayList<>();

    @JsonProperty("containers")
    private List<ContainerDetailDto> containers = new ArrayList<>();

    public PackingListDto() {}

    // Getters and Setters
    public String getPackingListNumber() { return packingListNumber; }
    public void setPackingListNumber(String packingListNumber) { this.packingListNumber = packingListNumber; }

    public String getPackingListDate() { return packingListDate; }
    public void setPackingListDate(String packingListDate) { this.packingListDate = packingListDate; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getConsigneeGstNumber() { return consigneeGstNumber; }
    public void setConsigneeGstNumber(String consigneeGstNumber) { this.consigneeGstNumber = consigneeGstNumber; }

    public String getExporterGstNumber() { return exporterGstNumber; }
    public void setExporterGstNumber(String exporterGstNumber) { this.exporterGstNumber = exporterGstNumber; }

    public String getCgstPercentage() { return cgstPercentage; }
    public void setCgstPercentage(String cgstPercentage) { this.cgstPercentage = cgstPercentage; }

    public String getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(String cgstAmount) { this.cgstAmount = cgstAmount; }

    public String getSgstPercentage() { return sgstPercentage; }
    public void setSgstPercentage(String sgstPercentage) { this.sgstPercentage = sgstPercentage; }

    public String getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(String sgstAmount) { this.sgstAmount = sgstAmount; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getShippingFrom() { return shippingFrom; }
    public void setShippingFrom(String shippingFrom) { this.shippingFrom = shippingFrom; }

    public String getShippingTo() { return shippingTo; }
    public void setShippingTo(String shippingTo) { this.shippingTo = shippingTo; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getExporterReference() { return exporterReference; }
    public void setExporterReference(String exporterReference) { this.exporterReference = exporterReference; }

    public String getIeCode() { return ieCode; }
    public void setIeCode(String ieCode) { this.ieCode = ieCode; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBuyerOrderNumber() { return buyerOrderNumber; }
    public void setBuyerOrderNumber(String buyerOrderNumber) { this.buyerOrderNumber = buyerOrderNumber; }

    public String getBuyerOrderDate() { return buyerOrderDate; }
    public void setBuyerOrderDate(String buyerOrderDate) { this.buyerOrderDate = buyerOrderDate; }

    public String getMainItemDescription() { return mainItemDescription; }
    public void setMainItemDescription(String mainItemDescription) { this.mainItemDescription = mainItemDescription; }

    public List<String> getOtherReferences() { return otherReferences; }
    public void setOtherReferences(List<String> otherReferences) { this.otherReferences = otherReferences != null ? otherReferences : new ArrayList<>(); }

    public PartyDto getShipperExporter() { return shipperExporter; }
    public void setShipperExporter(PartyDto shipperExporter) { this.shipperExporter = shipperExporter; }

    public PartyDto getVendor() { return vendor; }
    public void setVendor(PartyDto vendor) { this.vendor = vendor; }

    public PartyDto getConsignee() { return consignee; }
    public void setConsignee(PartyDto consignee) { this.consignee = consignee; }

    public PartyDto getBuyer() { return buyer; }
    public void setBuyer(PartyDto buyer) { this.buyer = buyer; }

    public PartyDto getNotifyParty() { return notifyParty; }
    public void setNotifyParty(PartyDto notifyParty) { this.notifyParty = notifyParty; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getCountryOfFinalDestination() { return countryOfFinalDestination; }
    public void setCountryOfFinalDestination(String countryOfFinalDestination) { this.countryOfFinalDestination = countryOfFinalDestination; }

    public String getPreCarriageBy() { return preCarriageBy; }
    public void setPreCarriageBy(String preCarriageBy) { this.preCarriageBy = preCarriageBy; }

    public String getPlaceOfReceipt() { return placeOfReceipt; }
    public void setPlaceOfReceipt(String placeOfReceipt) { this.placeOfReceipt = placeOfReceipt; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVoyageOrFlightNumber() { return voyageOrFlightNumber; }
    public void setVoyageOrFlightNumber(String voyageOrFlightNumber) { this.voyageOrFlightNumber = voyageOrFlightNumber; }

    public String getPortOfLoading() { return portOfLoading; }
    public void setPortOfLoading(String portOfLoading) { this.portOfLoading = portOfLoading; }

    public String getRouteOrPlaceOfTransshipment() { return routeOrPlaceOfTransshipment; }
    public void setRouteOrPlaceOfTransshipment(String routeOrPlaceOfTransshipment) { this.routeOrPlaceOfTransshipment = routeOrPlaceOfTransshipment; }

    public String getPortOfDischarge() { return portOfDischarge; }
    public void setPortOfDischarge(String portOfDischarge) { this.portOfDischarge = portOfDischarge; }

    public String getPlaceOfFinalDelivery() { return placeOfFinalDelivery; }
    public void setPlaceOfFinalDelivery(String placeOfFinalDelivery) { this.placeOfFinalDelivery = placeOfFinalDelivery; }

    public String getTypeOfMovement() { return typeOfMovement; }
    public void setTypeOfMovement(String typeOfMovement) { this.typeOfMovement = typeOfMovement; }

    public String getIncoterms() { return incoterms; }
    public void setIncoterms(String incoterms) { this.incoterms = incoterms; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getFreightTerms() { return freightTerms; }
    public void setFreightTerms(String freightTerms) { this.freightTerms = freightTerms; }

    public String getFreightAmount() { return freightAmount; }
    public void setFreightAmount(String freightAmount) { this.freightAmount = freightAmount; }

    public String getReleaseTerms() { return releaseTerms; }
    public void setReleaseTerms(String releaseTerms) { this.releaseTerms = releaseTerms; }

    public String getShipmentReference() { return shipmentReference; }
    public void setShipmentReference(String shipmentReference) { this.shipmentReference = shipmentReference; }

    public String getPlaceOfIssue() { return placeOfIssue; }
    public void setPlaceOfIssue(String placeOfIssue) { this.placeOfIssue = placeOfIssue; }

    public String getDateOfIssue() { return dateOfIssue; }
    public void setDateOfIssue(String dateOfIssue) { this.dateOfIssue = dateOfIssue; }

    public String getShippedOnBoardDate() { return shippedOnBoardDate; }
    public void setShippedOnBoardDate(String shippedOnBoardDate) { this.shippedOnBoardDate = shippedOnBoardDate; }

    public String getNumberOfOriginals() { return numberOfOriginals; }
    public void setNumberOfOriginals(String numberOfOriginals) { this.numberOfOriginals = numberOfOriginals; }

    public String getDeliveryContact() { return deliveryContact; }
    public void setDeliveryContact(String deliveryContact) { this.deliveryContact = deliveryContact; }

    public String getOtherParticulars() { return otherParticulars; }
    public void setOtherParticulars(String otherParticulars) { this.otherParticulars = otherParticulars; }

    public String getMarksAndNumbers() { return marksAndNumbers; }
    public void setMarksAndNumbers(String marksAndNumbers) { this.marksAndNumbers = marksAndNumbers; }

    public String getTotalPackageCount() { return totalPackageCount; }
    public void setTotalPackageCount(String totalPackageCount) { this.totalPackageCount = totalPackageCount; }

    public String getTotalPackageType() { return totalPackageType; }
    public void setTotalPackageType(String totalPackageType) { this.totalPackageType = totalPackageType; }

    public String getTotalGrossWeight() { return totalGrossWeight; }
    public void setTotalGrossWeight(String totalGrossWeight) { this.totalGrossWeight = totalGrossWeight; }

    public String getTotalNetWeight() { return totalNetWeight; }
    public void setTotalNetWeight(String totalNetWeight) { this.totalNetWeight = totalNetWeight; }

    public String getTotalMeasurement() { return totalMeasurement; }
    public void setTotalMeasurement(String totalMeasurement) { this.totalMeasurement = totalMeasurement; }

    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }

    public List<PackingListItemDto> getItems() { return items; }
    public void setItems(List<PackingListItemDto> items) { this.items = items != null ? items : new ArrayList<>(); }

    public List<ContainerDetailDto> getContainers() { return containers; }
    public void setContainers(List<ContainerDetailDto> containers) { this.containers = containers != null ? containers : new ArrayList<>(); }
}
