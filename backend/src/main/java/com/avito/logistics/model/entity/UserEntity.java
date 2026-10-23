package com.avito.logistics.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    @Column(name = "can_invoice_processing", nullable = false)
    private boolean canInvoiceProcessing = false;

    @Column(name = "can_mbl_processing", nullable = false)
    private boolean canMblProcessing = false;

    @Column(name = "created_at", nullable = false)
    private String createdAt = Instant.now().toString();

    public UserEntity() {}

    public UserEntity(String username, String passwordHash, boolean isAdmin, boolean canInvoiceProcessing, boolean canMblProcessing) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
        this.canInvoiceProcessing = canInvoiceProcessing;
        this.canMblProcessing = canMblProcessing;
        this.createdAt = Instant.now().toString();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public boolean isCanInvoiceProcessing() { return canInvoiceProcessing; }
    public void setCanInvoiceProcessing(boolean canInvoiceProcessing) { this.canInvoiceProcessing = canInvoiceProcessing; }

    public boolean isCanMblProcessing() { return canMblProcessing; }
    public void setCanMblProcessing(boolean canMblProcessing) { this.canMblProcessing = canMblProcessing; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
