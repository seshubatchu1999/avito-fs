import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { BackendApiService } from '../../core/services/backend-api.service';
import { ToastService } from '../../core/services/toast.service';
import { UserProfile } from '../../core/models/schemas';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="dashboard-container">
      <!-- Header -->
      <header class="dashboard-header">
        <div class="header-inner">
          <div class="header-left">
            <a routerLink="/" class="back-link">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5"/><polyline points="12 19 5 12 12 5"/></svg>
              <span>Workflows</span>
            </a>
            <div class="divider-v"></div>
            <img src="avito.png" alt="Avito Logo" class="header-logo" />
            <span class="portal-badge">Admin Portal</span>
          </div>

          <div class="header-right">
            @if (auth.currentUser(); as user) {
              <div class="admin-pill">
                <span class="avatar">{{ user.username.slice(0, 2).toUpperCase() }}</span>
                <span class="username">{{ user.username }}</span>
                <span class="badge-tag">Super Admin</span>
              </div>
            }
            <button class="btn-logout" (click)="logout()">Sign Out</button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="dashboard-content">
        <div class="content-wrapper">
          
          <!-- Title & Actions Bar -->
          <div class="title-bar">
            <div>
              <h1 class="page-title">User & Permission Governance</h1>
              <p class="page-subtitle">Manage user roles, audit accounts, and update module access permissions.</p>
            </div>
            <div class="action-buttons">
              <button class="btn-refresh" (click)="loadUsers()" [disabled]="loading()">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" [class.spin]="loading()"><path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/></svg>
                Refresh
              </button>
              <button class="btn-create-user" (click)="openCreateModal()">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Create New User
              </button>
            </div>
          </div>

          <!-- Stats KPI Cards -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon icon-blue">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">Total Users</span>
                <span class="stat-value">{{ users().length }}</span>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon icon-orange">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">Administrators</span>
                <span class="stat-value">{{ adminCount() }}</span>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon icon-teal">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="2,14 2,20 18,20 22,14" /><circle cx="6" cy="17" r="1.5"/><circle cx="12" cy="17" r="1.5"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">MBL Processing Authorized</span>
                <span class="stat-value">{{ mblCount() }}</span>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon icon-green">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">Invoice Processing Authorized</span>
                <span class="stat-value">{{ invoiceCount() }}</span>
              </div>
            </div>
          </div>

          <!-- Users Table Container -->
          <div class="table-card">
            <div class="table-toolbar">
              <div class="search-box">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                <input 
                  type="text" 
                  [(ngModel)]="searchQuery" 
                  placeholder="Filter by username..." 
                  class="search-input"
                />
              </div>
              <span class="results-count">Showing {{ filteredUsers().length }} of {{ users().length }} users</span>
            </div>

            @if (loading() && users().length === 0) {
              <div class="empty-state">
                <div class="spinner"></div>
                <p>Loading user accounts...</p>
              </div>
            } @else if (filteredUsers().length === 0) {
              <div class="empty-state">
                <p>No user accounts matched your search criteria.</p>
              </div>
            } @else {
              <div class="table-responsive">
                <table class="user-table">
                  <thead>
                    <tr>
                      <th>User Account</th>
                      <th>Account Role</th>
                      <th>MBL Module Access</th>
                      <th>Invoice Module Access</th>
                      <th>Registered On</th>
                      <th class="text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (user of filteredUsers(); track user.username) {
                      <tr>
                        <!-- Username with Avatar -->
                        <td>
                          <div class="user-cell">
                            <span class="avatar-sm">{{ user.username.slice(0, 2).toUpperCase() }}</span>
                            <div class="user-cell-meta">
                              <span class="user-name">{{ user.username }}</span>
                              @if (user.username === auth.currentUser()?.username) {
                                <span class="badge-you">(You)</span>
                              }
                            </div>
                          </div>
                        </td>

                        <!-- Role -->
                        <td>
                          <div class="role-switch">
                            <label class="toggle-control">
                              <input 
                                type="checkbox" 
                                [(ngModel)]="user.is_admin" 
                                (change)="onRoleToggle(user)" 
                              />
                              <span class="toggle-slider"></span>
                            </label>
                            <span class="role-label" [class.role-admin]="user.is_admin" [class.role-standard]="!user.is_admin">
                              {{ user.is_admin ? 'Admin' : 'Standard User' }}
                            </span>
                          </div>
                        </td>

                        <!-- MBL Permission -->
                        <td>
                          <label class="perm-checkbox-label">
                            <input 
                              type="checkbox" 
                              [(ngModel)]="user.can_mbl_processing" 
                              [disabled]="user.is_admin" 
                              class="checkbox-custom"
                            />
                            <span [class.active-text]="user.can_mbl_processing || user.is_admin">
                              {{ (user.can_mbl_processing || user.is_admin) ? 'Enabled' : 'Disabled' }}
                            </span>
                          </label>
                        </td>

                        <!-- Invoice Permission -->
                        <td>
                          <label class="perm-checkbox-label">
                            <input 
                              type="checkbox" 
                              [(ngModel)]="user.can_invoice_processing" 
                              [disabled]="user.is_admin" 
                              class="checkbox-custom"
                            />
                            <span [class.active-text]="user.can_invoice_processing || user.is_admin">
                              {{ (user.can_invoice_processing || user.is_admin) ? 'Enabled' : 'Disabled' }}
                            </span>
                          </label>
                        </td>

                        <!-- Date -->
                        <td class="date-cell">
                          {{ formatDate(user.created_at) }}
                        </td>

                        <!-- Save Action Button -->
                        <td class="text-right">
                          <button 
                            class="btn-save" 
                            (click)="savePermissions(user)"
                            [disabled]="savingUser() === user.username"
                          >
                            {{ savingUser() === user.username ? 'Saving...' : 'Save' }}
                          </button>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>

        </div>
      </main>

      <!-- Create New User Modal -->
      @if (showCreateModal()) {
        <div class="modal-backdrop" (click)="closeCreateModal()">
          <div class="modal-card" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>Create User Account</h3>
              <button class="modal-close" (click)="closeCreateModal()">&times;</button>
            </div>

            <form (ngSubmit)="submitCreateUser()" class="modal-form">
              <label class="form-label">Username</label>
              <input 
                type="text" 
                [(ngModel)]="newUsername" 
                name="username" 
                class="form-input" 
                placeholder="e.g. logistics_operator" 
                required 
              />

              <label class="form-label">Password</label>
              <input 
                type="password" 
                [(ngModel)]="newPassword" 
                name="password" 
                class="form-input" 
                placeholder="Minimum 6 characters" 
                required 
              />

              <div class="form-group-checkboxes">
                <span class="group-title">Module Permissions & Roles</span>
                
                <label class="modal-checkbox-row">
                  <input type="checkbox" [(ngModel)]="newIsAdmin" name="isAdmin" />
                  <div>
                    <strong>Administrator Account</strong>
                    <p class="checkbox-desc">Full access to all workflows and user management.</p>
                  </div>
                </label>

                @if (!newIsAdmin) {
                  <label class="modal-checkbox-row">
                    <input type="checkbox" [(ngModel)]="newCanMbl" name="canMbl" />
                    <div>
                      <strong>MBL Processing Module</strong>
                      <p class="checkbox-desc">Authorize access to packing list extractions and HBL/MBL generation.</p>
                    </div>
                  </label>

                  <label class="modal-checkbox-row">
                    <input type="checkbox" [(ngModel)]="newCanInvoice" name="canInvoice" />
                    <div>
                      <strong>Vendor Invoice Processing Module</strong>
                      <p class="checkbox-desc">Authorize access to financial invoice extraction and spreadsheet export.</p>
                    </div>
                  </label>
                }
              </div>

              <div class="modal-actions">
                <button type="button" class="btn-cancel" (click)="closeCreateModal()">Cancel</button>
                <button type="submit" class="btn-primary" [disabled]="submitting()">
                  {{ submitting() ? 'Creating...' : 'Create Account' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

    </div>
  `,
  styles: [`
    .dashboard-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
      background: #f8fafc;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }
    .dashboard-header {
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(12px);
      border-bottom: 1px solid #e2e8f0;
      padding: 0.75rem 2rem;
      position: sticky;
      top: 0;
      z-index: 100;
    }
    .header-inner {
      max-width: 1400px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .header-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .back-link {
      display: flex;
      align-items: center;
      gap: 0.4rem;
      color: #64748b;
      text-decoration: none;
      font-weight: 500;
      font-size: 0.875rem;
      transition: color 0.2s;
    }
    .back-link:hover {
      color: #f26d21;
    }
    .divider-v {
      width: 1px;
      height: 20px;
      background: #cbd5e1;
    }
    .header-logo {
      height: 36px;
      object-fit: contain;
    }
    .portal-badge {
      font-size: 0.75rem;
      font-weight: 700;
      text-transform: uppercase;
      background: #fef1eb;
      color: #f26d21;
      padding: 3px 8px;
      border-radius: 6px;
      border: 1px solid rgba(242, 109, 33, 0.2);
    }
    .header-right {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .admin-pill {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: #ffffff;
      padding: 0.35rem 0.85rem;
      border-radius: 9999px;
      border: 1px solid #e2e8f0;
      box-shadow: 0 1px 2px rgba(0,0,0,0.03);
    }
    .avatar {
      width: 26px;
      height: 26px;
      border-radius: 50%;
      background: #f26d21;
      color: white;
      font-size: 0.75rem;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .username {
      font-size: 0.875rem;
      font-weight: 600;
      color: #1e293b;
    }
    .badge-tag {
      font-size: 0.65rem;
      font-weight: 700;
      text-transform: uppercase;
      background: #dbeafe;
      color: #1d4ed8;
      padding: 2px 6px;
      border-radius: 4px;
    }
    .btn-logout {
      font-size: 0.85rem;
      font-weight: 500;
      color: #64748b;
      background: none;
      border: 1px solid #cbd5e1;
      padding: 0.4rem 0.85rem;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
    }
    .btn-logout:hover {
      background: #fee2e2;
      color: #dc2626;
      border-color: #fca5a5;
    }

    /* Content Area */
    .dashboard-content {
      flex: 1;
      padding: 2.5rem 2rem;
    }
    .content-wrapper {
      max-width: 1400px;
      margin: 0 auto;
    }
    .title-bar {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: 2rem;
    }
    .page-title {
      font-size: 1.85rem;
      font-weight: 800;
      color: #1a2b4c;
      margin: 0 0 0.4rem 0;
      letter-spacing: -0.02em;
    }
    .page-subtitle {
      color: #64748b;
      margin: 0;
      font-size: 0.95rem;
    }
    .action-buttons {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .btn-refresh {
      display: flex;
      align-items: center;
      gap: 0.4rem;
      padding: 0.65rem 1rem;
      background: #ffffff;
      color: #334155;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.875rem;
      cursor: pointer;
      transition: all 0.2s;
    }
    .btn-refresh:hover {
      background: #f1f5f9;
      border-color: #94a3b8;
    }
    .btn-create-user {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.65rem 1.25rem;
      background: #f26d21;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.875rem;
      cursor: pointer;
      box-shadow: 0 4px 6px -1px rgba(242, 109, 33, 0.25);
      transition: all 0.2s;
    }
    .btn-create-user:hover {
      background: #d95a16;
      transform: translateY(-1px);
    }

    /* KPI Cards */
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 1.25rem;
      margin-bottom: 2rem;
    }
    .stat-card {
      background: #ffffff;
      border-radius: 12px;
      padding: 1.25rem 1.5rem;
      display: flex;
      align-items: center;
      gap: 1.25rem;
      border: 1px solid #e2e8f0;
      box-shadow: 0 1px 3px rgba(0,0,0,0.04);
    }
    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .icon-blue { background: #eff6ff; color: #2563eb; }
    .icon-orange { background: #fef1eb; color: #f26d21; }
    .icon-teal { background: #f0fdfa; color: #0d9488; }
    .icon-green { background: #f0fdf4; color: #16a34a; }

    .stat-info {
      display: flex;
      flex-direction: column;
    }
    .stat-label {
      font-size: 0.8rem;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.04em;
    }
    .stat-value {
      font-size: 1.85rem;
      font-weight: 800;
      color: #1a2b4c;
      line-height: 1.2;
    }

    /* Table Container */
    .table-card {
      background: #ffffff;
      border-radius: 14px;
      border: 1px solid #e2e8f0;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
      overflow: hidden;
    }
    .table-toolbar {
      padding: 1.25rem 1.5rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid #e2e8f0;
      background: #fafafa;
    }
    .search-box {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: white;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      padding: 0.45rem 0.85rem;
      width: 320px;
      color: #64748b;
    }
    .search-input {
      border: none;
      outline: none;
      font-size: 0.875rem;
      width: 100%;
    }
    .results-count {
      font-size: 0.85rem;
      color: #64748b;
    }
    .table-responsive {
      overflow-x: auto;
    }
    .user-table {
      width: 100%;
      border-collapse: collapse;
      text-align: left;
      font-size: 0.875rem;
    }
    .user-table th {
      padding: 1rem 1.5rem;
      background: #f8fafc;
      color: #475569;
      font-weight: 600;
      border-bottom: 1px solid #e2e8f0;
      text-transform: uppercase;
      font-size: 0.75rem;
      letter-spacing: 0.05em;
    }
    .user-table td {
      padding: 1rem 1.5rem;
      border-bottom: 1px solid #f1f5f9;
      color: #1e293b;
      vertical-align: middle;
    }
    .user-cell {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .avatar-sm {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #e2e8f0;
      color: #334155;
      font-weight: 700;
      font-size: 0.75rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .user-name {
      font-weight: 600;
      color: #0f172a;
    }
    .badge-you {
      font-size: 0.7rem;
      color: #f26d21;
      font-weight: 700;
      margin-left: 0.35rem;
    }
    .role-switch {
      display: flex;
      align-items: center;
      gap: 0.6rem;
    }
    .role-label {
      font-size: 0.8rem;
      font-weight: 600;
      padding: 2px 8px;
      border-radius: 4px;
    }
    .role-admin { background: #fef1eb; color: #f26d21; }
    .role-standard { background: #f1f5f9; color: #475569; }

    /* Custom Toggle Switch */
    .toggle-control {
      position: relative;
      display: inline-block;
      width: 38px;
      height: 20px;
    }
    .toggle-control input {
      opacity: 0;
      width: 0;
      height: 0;
    }
    .toggle-slider {
      position: absolute;
      cursor: pointer;
      top: 0; left: 0; right: 0; bottom: 0;
      background-color: #cbd5e1;
      transition: .3s;
      border-radius: 20px;
    }
    .toggle-slider:before {
      position: absolute;
      content: "";
      height: 14px;
      width: 14px;
      left: 3px;
      bottom: 3px;
      background-color: white;
      transition: .3s;
      border-radius: 50%;
    }
    input:checked + .toggle-slider {
      background-color: #f26d21;
    }
    input:checked + .toggle-slider:before {
      transform: translateX(18px);
    }

    .perm-checkbox-label {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.85rem;
      cursor: pointer;
      color: #64748b;
    }
    .perm-checkbox-label .active-text {
      color: #16a34a;
      font-weight: 600;
    }
    .checkbox-custom {
      width: 16px;
      height: 16px;
      accent-color: #f26d21;
      cursor: pointer;
    }
    .date-cell {
      color: #64748b;
      font-size: 0.85rem;
    }
    .text-right {
      text-align: right;
    }
    .btn-save {
      padding: 0.4rem 1rem;
      background: #0284c7;
      color: white;
      border: none;
      border-radius: 6px;
      font-weight: 600;
      font-size: 0.8rem;
      cursor: pointer;
      transition: background 0.2s;
    }
    .btn-save:hover {
      background: #0369a1;
    }
    .btn-save:disabled {
      background: #94a3b8;
      cursor: not-allowed;
    }

    .empty-state {
      padding: 3rem;
      text-align: center;
      color: #64748b;
    }
    .spinner {
      width: 32px;
      height: 32px;
      border: 3px solid #e2e8f0;
      border-top-color: #f26d21;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
      margin: 0 auto 1rem auto;
    }
    .spin {
      animation: spin 0.8s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    /* Modal Styles */
    .modal-backdrop {
      position: fixed;
      inset: 0;
      background: rgba(15, 23, 42, 0.6);
      backdrop-filter: blur(4px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }
    .modal-card {
      background: white;
      border-radius: 16px;
      width: 100%;
      max-width: 480px;
      padding: 2rem;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    }
    .modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 1.5rem;
    }
    .modal-header h3 {
      font-size: 1.3rem;
      font-weight: 700;
      color: #1a2b4c;
      margin: 0;
    }
    .modal-close {
      background: none;
      border: none;
      font-size: 1.5rem;
      color: #94a3b8;
      cursor: pointer;
    }
    .form-label {
      display: block;
      font-size: 0.875rem;
      font-weight: 600;
      color: #334155;
      margin: 1rem 0 0.35rem 0;
    }
    .form-input {
      width: 100%;
      box-sizing: border-box;
      padding: 0.65rem 0.85rem;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      font-size: 0.95rem;
    }
    .form-group-checkboxes {
      margin-top: 1.5rem;
      padding: 1rem;
      background: #f8fafc;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
    }
    .group-title {
      display: block;
      font-size: 0.8rem;
      font-weight: 700;
      text-transform: uppercase;
      color: #475569;
      margin-bottom: 0.75rem;
    }
    .modal-checkbox-row {
      display: flex;
      align-items: flex-start;
      gap: 0.75rem;
      margin-bottom: 0.75rem;
      cursor: pointer;
    }
    .modal-checkbox-row input {
      margin-top: 3px;
      width: 16px;
      height: 16px;
      accent-color: #f26d21;
    }
    .checkbox-desc {
      font-size: 0.75rem;
      color: #64748b;
      margin: 2px 0 0 0;
    }
    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 0.75rem;
      margin-top: 1.75rem;
    }
    .btn-cancel {
      padding: 0.65rem 1.25rem;
      background: #ffffff;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      font-weight: 600;
      color: #475569;
      cursor: pointer;
    }
    .btn-primary {
      padding: 0.65rem 1.5rem;
      background: #f26d21;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
    }
    .btn-primary:hover {
      background: #d95a16;
    }
  `]
})
export class AdminDashboardComponent implements OnInit {
  users = signal<UserProfile[]>([]);
  searchQuery = '';
  loading = signal(false);
  savingUser = signal<string | null>(null);

  // Modal
  showCreateModal = signal(false);
  newUsername = '';
  newPassword = '';
  newIsAdmin = false;
  newCanMbl = true;
  newCanInvoice = true;
  submitting = signal(false);

  // Computed KPIs
  adminCount = computed(() => this.users().filter(u => u.is_admin).length);
  mblCount = computed(() => this.users().filter(u => u.is_admin || u.can_mbl_processing).length);
  invoiceCount = computed(() => this.users().filter(u => u.is_admin || u.can_invoice_processing).length);

  filteredUsers = computed(() => {
    const query = this.searchQuery.toLowerCase().trim();
    if (!query) return this.users();
    return this.users().filter(u => u.username.toLowerCase().includes(query));
  });

  constructor(
    public auth: AuthService,
    private backendApi: BackendApiService,
    private toast: ToastService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading.set(true);
    this.backendApi.listUsers().subscribe({
      next: (list) => {
        this.users.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.show('Failed to load user accounts: ' + (err.error?.message || 'Access restricted'), 'error');
      }
    });
  }

  onRoleToggle(user: UserProfile) {
    if (user.is_admin) {
      user.can_mbl_processing = true;
      user.can_invoice_processing = true;
    }
  }

  savePermissions(user: UserProfile) {
    this.savingUser.set(user.username);
    this.auth.updateUserPermissions(user.username, {
      is_admin: user.is_admin,
      can_mbl_processing: user.can_mbl_processing,
      can_invoice_processing: user.can_invoice_processing
    }).subscribe({
      next: (updated) => {
        this.savingUser.set(null);
        this.toast.show(`Updated permissions for ${user.username}`, 'success');
        // Update local list
        this.users.update(list => list.map(u => u.username === updated.username ? updated : u));
      },
      error: (err) => {
        this.savingUser.set(null);
        this.toast.show('Error updating permissions: ' + (err.error?.message || err.message), 'error');
      }
    });
  }

  openCreateModal() {
    this.newUsername = '';
    this.newPassword = '';
    this.newIsAdmin = false;
    this.newCanMbl = true;
    this.newCanInvoice = true;
    this.showCreateModal.set(true);
  }

  closeCreateModal() {
    this.showCreateModal.set(false);
  }

  submitCreateUser() {
    if (!this.newUsername || !this.newPassword) {
      this.toast.show('Username and password are required.', 'error');
      return;
    }

    this.submitting.set(true);
    this.backendApi.register({
      username: this.newUsername,
      password: this.newPassword,
      is_admin: this.newIsAdmin,
      can_mbl_processing: this.newCanMbl,
      can_invoice_processing: this.newCanInvoice
    }).subscribe({
      next: (created) => {
        this.submitting.set(false);
        this.toast.show(`Account '${created.username}' created successfully!`, 'success');
        this.closeCreateModal();
        this.loadUsers();
      },
      error: (err) => {
        this.submitting.set(false);
        this.toast.show('Registration error: ' + (err.error?.message || err.error?.error || 'Failed to create user'), 'error');
      }
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  formatDate(isoString?: string): string {
    if (!isoString) return 'N/A';
    try {
      const d = new Date(isoString);
      return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoString;
    }
  }
}