package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackingListItemDto {

    @JsonProperty("source_invoice_number")
    private String sourceInvoiceNumber;

    @JsonProperty("source_filename")
    private String sourceFilename;

    @JsonProperty("source_po_number")
    private String sourcePoNumber;

    @JsonProperty("source_total_package_count")
    private String sourceTotalPackageCount;

    @JsonProperty("source_total_package_type")
    private String sourceTotalPackageType;

    @JsonProperty("source_main_item_description")
    private String sourceMainItemDescription;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("item_product_description")
    private String itemProductDescription;

    @JsonProperty("package_numbers")
    private String packageNumbers;

    @JsonProperty("index_number")
    private String indexNumber;

    @JsonProperty("hsn_code")
    private String hsnCode;

    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("taxable_value")
    private String taxableValue;

    @JsonProperty("rate")
    private String rate;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    @JsonProperty("cgst_percentage")
    private String cgstPercentage;

    @JsonProperty("cgst_amount")
    private String cgstAmount;

    @JsonProperty("sgst_percentage")
    private String sgstPercentage;

    @JsonProperty("sgst_amount")
    private String sgstAmount;

    @JsonProperty("package_count")
    private String packageCount;

    @JsonProperty("package_type")
    private String packageType;

    @JsonProperty("gross_weight")
    private String grossWeight;

    @JsonProperty("net_weight")
    private String netWeight;

    @JsonProperty("dimensions")
    private String dimensions;

    @JsonProperty("cbm_volume")
    private String cbmVolume;

    @JsonProperty("container_number")
    private String containerNumber;

    @JsonProperty("seal_number")
    private String sealNumber;

    @JsonProperty("shipping_marks")
    private String shippingMarks;

    public PackingListItemDto() {}

    // Getters and Setters
    public String getSourceInvoiceNumber() { return sourceInvoiceNumber; }
    public void setSourceInvoiceNumber(String sourceInvoiceNumber) { this.sourceInvoiceNumber = sourceInvoiceNumber; }

    public String getSourceFilename() { return sourceFilename; }
    public void setSourceFilename(String sourceFilename) { this.sourceFilename = sourceFilename; }

    public String getSourcePoNumber() { return sourcePoNumber; }
    public void setSourcePoNumber(String sourcePoNumber) { this.sourcePoNumber = sourcePoNumber; }

    public String getSourceTotalPackageCount() { return sourceTotalPackageCount; }
    public void setSourceTotalPackageCount(String sourceTotalPackageCount) { this.sourceTotalPackageCount = sourceTotalPackageCount; }

    public String getSourceTotalPackageType() { return sourceTotalPackageType; }
    public void setSourceTotalPackageType(String sourceTotalPackageType) { this.sourceTotalPackageType = sourceTotalPackageType; }

    public String getSourceMainItemDescription() { return sourceMainItemDescription; }
    public void setSourceMainItemDescription(String sourceMainItemDescription) { this.sourceMainItemDescription = sourceMainItemDescription; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getItemProductDescription() { return itemProductDescription; }
    public void setItemProductDescription(String itemProductDescription) { this.itemProductDescription = itemProductDescription; }

    public String getPackageNumbers() { return packageNumbers; }
    public void setPackageNumbers(String packageNumbers) { this.packageNumbers = packageNumbers; }

    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getTaxableValue() { return taxableValue; }
    public void setTaxableValue(String taxableValue) { this.taxableValue = taxableValue; }

    public String getRate() { return rate; }
    public void setRate(String rate) { this.rate = rate; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public String getCgstPercentage() { return cgstPercentage; }
    public void setCgstPercentage(String cgstPercentage) { this.cgstPercentage = cgstPercentage; }

    public String getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(String cgstAmount) { this.cgstAmount = cgstAmount; }

    public String getSgstPercentage() { return sgstPercentage; }
    public void setSgstPercentage(String sgstPercentage) { this.sgstPercentage = sgstPercentage; }

    public String getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(String sgstAmount) { this.sgstAmount = sgstAmount; }

    public String getPackageCount() { return packageCount; }
    public void setPackageCount(String packageCount) { this.packageCount = packageCount; }

    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public String getGrossWeight() { return grossWeight; }
    public void setGrossWeight(String grossWeight) { this.grossWeight = grossWeight; }

    public String getNetWeight() { return netWeight; }
    public void setNetWeight(String netWeight) { this.netWeight = netWeight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getCbmVolume() { return cbmVolume; }
    public void setCbmVolume(String cbmVolume) { this.cbmVolume = cbmVolume; }

    public String getContainerNumber() { return containerNumber; }
    public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }

    public String getSealNumber() { return sealNumber; }
    public void setSealNumber(String sealNumber) { this.sealNumber = sealNumber; }

    public String getShippingMarks() { return shippingMarks; }
    public void setShippingMarks(String shippingMarks) { this.shippingMarks = shippingMarks; }
}
