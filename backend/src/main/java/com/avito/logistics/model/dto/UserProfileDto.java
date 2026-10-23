package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("is_admin")
    private boolean isAdmin;

    @JsonProperty("can_invoice_processing")
    private boolean canInvoiceProcessing;

    @JsonProperty("can_mbl_processing")
    private boolean canMblProcessing;

    @JsonProperty("created_at")
    private String createdAt;

    public UserProfileDto() {}

    public UserProfileDto(String username, boolean isAdmin, boolean canInvoiceProcessing, boolean canMblProcessing, String createdAt) {
        this.username = username;
        this.isAdmin = isAdmin;
        this.canInvoiceProcessing = canInvoiceProcessing;
        this.canMblProcessing = canMblProcessing;
        this.createdAt = createdAt;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public boolean isCanInvoiceProcessing() { return canInvoiceProcessing; }
    public void setCanInvoiceProcessing(boolean canInvoiceProcessing) { this.canInvoiceProcessing = canInvoiceProcessing; }

    public boolean isCanMblProcessing() { return canMblProcessing; }
    public void setCanMblProcessing(boolean canMblProcessing) { this.canMblProcessing = canMblProcessing; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
