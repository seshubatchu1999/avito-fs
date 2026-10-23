package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceSheetDto {

    @JsonProperty("invoice_date")
    private String invoiceDate;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("vendor_name")
    private String vendorName;

    @JsonProperty("vendor_gst_number")
    private String vendorGstNumber;

    @JsonProperty("taxable_value")
    private String taxableValue;

    @JsonProperty("gst_value")
    private String gstValue;

    @JsonProperty("total_inv_value")
    private String totalInvValue;

    @JsonProperty("container_number")
    private String containerNumber;

    public InvoiceSheetDto() {}

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getVendorGstNumber() { return vendorGstNumber; }
    public void setVendorGstNumber(String vendorGstNumber) { this.vendorGstNumber = vendorGstNumber; }

    public String getTaxableValue() { return taxableValue; }
    public void setTaxableValue(String taxableValue) { this.taxableValue = taxableValue; }

    public String getGstValue() { return gstValue; }
    public void setGstValue(String gstValue) { this.gstValue = gstValue; }

    public String getTotalInvValue() { return totalInvValue; }
    public void setTotalInvValue(String totalInvValue) { this.totalInvValue = totalInvValue; }

    public String getContainerNumber() { return containerNumber; }
    public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }
}
