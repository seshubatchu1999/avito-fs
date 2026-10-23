package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterRequestDto {

    @NotBlank
    @Size(min = 3, max = 50)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    @JsonProperty("password")
    private String password;

    @JsonProperty("is_admin")
    private Boolean isAdmin;

    @JsonProperty("can_invoice_processing")
    private Boolean canInvoiceProcessing;

    @JsonProperty("can_mbl_processing")
    private Boolean canMblProcessing;

    public UserRegisterRequestDto() {}

    public UserRegisterRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getCanInvoiceProcessing() { return canInvoiceProcessing; }
    public void setCanInvoiceProcessing(Boolean canInvoiceProcessing) { this.canInvoiceProcessing = canInvoiceProcessing; }

    public Boolean getCanMblProcessing() { return canMblProcessing; }
    public void setCanMblProcessing(Boolean canMblProcessing) { this.canMblProcessing = canMblProcessing; }
}