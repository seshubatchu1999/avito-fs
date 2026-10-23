package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePermissionsRequestDto {

    @JsonProperty("can_invoice_processing")
    private Boolean canInvoiceProcessing;

    @JsonProperty("can_mbl_processing")
    private Boolean canMblProcessing;

    @JsonProperty("is_admin")
    private Boolean isAdmin;

    public UpdatePermissionsRequestDto() {}

    public Boolean getCanInvoiceProcessing() { return canInvoiceProcessing; }
    public void setCanInvoiceProcessing(Boolean canInvoiceProcessing) { this.canInvoiceProcessing = canInvoiceProcessing; }

    public Boolean getCanMblProcessing() { return canMblProcessing; }
    public void setCanMblProcessing(Boolean canMblProcessing) { this.canMblProcessing = canMblProcessing; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
}