import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="home-container">
      <header class="app-header">
        <div class="header-inner">
          <div class="logo-box">
            <img src="avito.png" alt="Avito Logo" class="header-logo" />
          </div>

          <!-- Auth & User State Controls -->
          <div class="auth-bar">
            @if (auth.isLoggedIn(); as loggedIn) {
              <div class="user-pill">
                <span class="user-avatar">{{ userInitials() }}</span>
                <span class="username">{{ auth.currentUser()?.username }}</span>
                @if (auth.currentUser()?.is_admin) {
                  <span class="admin-badge">Admin</span>
                } @else {
                  <span class="user-badge">User</span>
                }
              </div>

              @if (auth.currentUser()?.is_admin) {
                <a routerLink="/admin" class="btn-admin-portal">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                  Admin Dashboard
                </a>
              }

              <button class="btn-logout" (click)="logout()">Sign Out</button>
            } @else {
              <!-- Explicit Sign In Options -->
              <button class="btn-login-user" (click)="openAuthModal('login', false)">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Login as User
              </button>
              
              <button class="btn-login-admin" (click)="openAuthModal('login', true)">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                Login as Admin
              </button>

              <button class="btn-register" (click)="openAuthModal('register', false)">Register</button>
            }
          </div>
        </div>
      </header>

      <main class="main-content">
        <div class="hero-section">
          <h1 class="welcome-title">Welcome to <span class="brand-text">Avito Processing</span></h1>
          <p class="subtitle">Please select a workflow to begin your logistics session</p>
        </div>

        <div class="options-grid">
          
          <!-- MBL Flow Card -->
          <div 
            class="option-card"
            [class.card-disabled]="auth.isLoggedIn() && !auth.canProcessMbl()"
            (click)="navigateToWorkflow('/mbl-processing', auth.canProcessMbl(), 'MBL Processing')"
          >
            <div class="card-glow"></div>
            <div class="icon-wrapper">
              <!-- Ship Silhouette SVG -->
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 64 64" fill="currentColor">
                <polygon points="2,38 2,54 52,54 62,38" />
                <circle cx="12" cy="46" r="2.5" fill="#eff6ff" />
                <circle cx="24" cy="46" r="2.5" fill="#eff6ff" />
                <circle cx="36" cy="46" r="2.5" fill="#eff6ff" />
                <circle cx="48" cy="46" r="2.5" fill="#eff6ff" />
                <rect x="11" y="25" width="2" height="13" />
                <rect x="16" y="33" width="10" height="4" />
                <rect x="16" y="28" width="10" height="4" />
                <rect x="16" y="23" width="10" height="4" />
                <rect x="27" y="33" width="10" height="4" />
                <rect x="27" y="28" width="10" height="4" />
                <rect x="27" y="23" width="10" height="4" />
                <rect x="37" y="34" width="7" height="4" />
                <rect x="39" y="10" width="3" height="26" />
                <rect x="22" y="10" width="17" height="2" />
                <rect x="23" y="12" width="1" height="4" />
                <path d="M 22.5 16 C 22.5 18, 24.5 18, 24.5 16" fill="none" stroke="currentColor" stroke-width="1.5" />
                <polygon points="43,38 45,22 55,22 57,38" />
                <rect x="46.5" y="24" width="1.5" height="2" fill="#eff6ff" />
                <rect x="49.25" y="24" width="1.5" height="2" fill="#eff6ff" />
                <rect x="52" y="24" width="1.5" height="2" fill="#eff6ff" />
                <rect x="46" y="28" width="1.5" height="2" fill="#eff6ff" />
                <rect x="49.25" y="28" width="1.5" height="2" fill="#eff6ff" />
                <rect x="52.5" y="28" width="1.5" height="2" fill="#eff6ff" />
                <rect x="45.5" y="32" width="1.5" height="2" fill="#eff6ff" />
                <rect x="49.25" y="32" width="1.5" height="2" fill="#eff6ff" />
                <rect x="53" y="32" width="1.5" height="2" fill="#eff6ff" />
              </svg>
            </div>
            <div class="card-content">
              <div class="card-title-row">
                <h2>MBL Processing</h2>
                @if (auth.isLoggedIn()) {
                  <span class="status-tag" [class.status-tag-active]="auth.canProcessMbl()" [class.status-tag-locked]="!auth.canProcessMbl()">
                    {{ auth.canProcessMbl() ? 'Authorized' : 'Permission Required' }}
                  </span>
                }
              </div>
              <p>Upload packing lists and complete HBL/MBL details efficiently through an automated workflow.</p>
            </div>
            <div class="card-arrow">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
            </div>
          </div>

          <!-- Vendor Invoice Card -->
          <div 
            class="option-card"
            [class.card-disabled]="auth.isLoggedIn() && !auth.canProcessInvoices()"
            (click)="navigateToWorkflow('/invoice-processing', auth.canProcessInvoices(), 'Vendor Invoice Processing')"
          >
            <div class="card-glow"></div>
            <div class="icon-wrapper">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
            </div>
            <div class="card-content">
              <div class="card-title-row">
                <h2>Vendor Invoice Processing</h2>
                @if (auth.isLoggedIn()) {
                  <span class="status-tag" [class.status-tag-active]="auth.canProcessInvoices()" [class.status-tag-locked]="!auth.canProcessInvoices()">
                    {{ auth.canProcessInvoices() ? 'Authorized' : 'Permission Required' }}
                  </span>
                }
              </div>
              <p>Upload invoices to automatically extract, review, and export structured financial data.</p>
            </div>
            <div class="card-arrow">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
            </div>
          </div>

        </div>
      </main>

      <!-- Auth Modal -->
      @if (showAuthModal()) {
        <div class="modal-backdrop" (click)="closeAuthModal()">
          <div class="modal-card" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>{{ authTab() === 'login' ? (targetIsAdmin ? 'Administrator Sign In' : 'User Sign In') : (targetIsAdmin ? 'Register Administrator' : 'Create User Account') }}</h3>
              <button class="modal-close" (click)="closeAuthModal()">&times;</button>
            </div>

            <!-- Mode Selector -->
            <div class="auth-mode-selector">
              <button 
                type="button" 
                [class.active]="!targetIsAdmin" 
                (click)="targetIsAdmin = false"
              >
                Standard User
              </button>
              <button 
                type="button" 
                [class.active]="targetIsAdmin" 
                (click)="targetIsAdmin = true"
              >
                Administrator
              </button>
            </div>

            <div class="modal-tabs">
              <button [class.active]="authTab() === 'login'" (click)="authTab.set('login')">Sign In</button>
              <button [class.active]="authTab() === 'register'" (click)="authTab.set('register')">Register</button>
            </div>

            <form (ngSubmit)="submitAuth()" class="modal-body">
              <label class="form-label">Username</label>
              <input 
                type="text" 
                [(ngModel)]="authUsername" 
                name="username" 
                class="form-input" 
                [placeholder]="targetIsAdmin ? 'e.g. admin_user' : 'e.g. logistics_user'" 
                required 
              />

              <label class="form-label">Password</label>
              <input 
                type="password" 
                [(ngModel)]="authPassword" 
                name="password" 
                class="form-input" 
                placeholder="Password (minimum 6 characters)" 
                required 
              />

              @if (authTab() === 'register' && !targetIsAdmin) {
                <div class="permissions-preview">
                  <span class="preview-title">Initial Permissions:</span>
                  <label class="checkbox-label">
                    <input type="checkbox" [(ngModel)]="regCanMbl" name="canMbl" />
                    <span>MBL Processing Access</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" [(ngModel)]="regCanInvoice" name="canInvoice" />
                    <span>Vendor Invoice Processing Access</span>
                  </label>
                </div>
              }

              <div class="modal-actions">
                <button type="submit" class="btn-primary" [disabled]="loading()">
                  {{ loading() ? 'Processing...' : (authTab() === 'login' ? (targetIsAdmin ? 'Sign In as Admin' : 'Sign In as User') : (targetIsAdmin ? 'Register as Admin' : 'Register Account')) }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

    </div>
  `,
  styles: [`
    .home-container {
      display: flex;
      flex-direction: column;
      height: 100vh;
      overflow: hidden;
      background: radial-gradient(circle at 0% 0%, rgba(242, 109, 33, 0.04) 0%, transparent 40%),
                  radial-gradient(circle at 100% 100%, rgba(26, 43, 76, 0.04) 0%, transparent 40%),
                  #f8fafc;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
    }
    .app-header {
      flex-shrink: 0;
      background: rgba(255, 255, 255, 0.85);
      backdrop-filter: blur(12px);
      padding: 0.85rem 2.5rem;
      border-bottom: 1px solid rgba(226, 232, 240, 0.8);
      z-index: 10;
    }
    .header-inner {
      max-width: 1400px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .header-logo {
      height: 38px;
      object-fit: contain;
    }
    .auth-bar {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .user-pill {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.35rem 0.75rem;
      background: #f1f5f9;
      border-radius: 9999px;
      border: 1px solid #e2e8f0;
      font-size: 0.875rem;
      font-weight: 500;
      color: #1e293b;
    }
    .user-avatar {
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
      text-transform: uppercase;
    }
    .admin-badge {
      font-size: 0.65rem;
      font-weight: 700;
      background: #fef1eb;
      color: #f26d21;
      padding: 2px 6px;
      border-radius: 4px;
      border: 1px solid rgba(242, 109, 33, 0.3);
      text-transform: uppercase;
    }
    .user-badge {
      font-size: 0.65rem;
      font-weight: 600;
      background: #e2e8f0;
      color: #475569;
      padding: 2px 6px;
      border-radius: 4px;
      text-transform: uppercase;
    }
    .btn-login-user, .btn-login-admin, .btn-register, .btn-admin-portal, .btn-logout {
      font-size: 0.875rem;
      font-weight: 600;
      padding: 0.45rem 1rem;
      border-radius: 8px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      transition: all 0.2s;
      text-decoration: none;
    }
    .btn-login-user {
      background: #ffffff;
      color: #334155;
      border: 1px solid #cbd5e1;
    }
    .btn-login-user:hover {
      background: #f8fafc;
      border-color: #94a3b8;
    }
    .btn-login-admin {
      background: #1a2b4c;
      color: #ffffff;
      border: 1px solid #1a2b4c;
    }
    .btn-login-admin:hover {
      background: #0f1c34;
    }
    .btn-register {
      background: #f26d21;
      color: white;
      border: 1px solid #f26d21;
    }
    .btn-register:hover {
      background: #d95a16;
    }
    .btn-admin-portal {
      background: #fef1eb;
      color: #f26d21;
      border: 1px solid rgba(242, 109, 33, 0.3);
    }
    .btn-admin-portal:hover {
      background: #fddcd0;
    }
    .btn-logout {
      background: transparent;
      color: #64748b;
      border: 1px solid transparent;
    }
    .btn-logout:hover {
      background: #fee2e2;
      color: #dc2626;
    }
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 2rem;
      overflow-y: auto;
    }
    .hero-section {
      text-align: center;
      margin-bottom: 3.5rem;
      animation: fadeInDown 0.6s ease-out;
    }
    .welcome-title {
      font-size: 3rem;
      color: #1a2b4c;
      margin-bottom: 1rem;
      font-weight: 800;
      letter-spacing: -0.025em;
    }
    .brand-text {
      background: linear-gradient(135deg, #f26d21, #d95a16);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      color: transparent;
    }
    .subtitle {
      font-size: 1.15rem;
      color: #64748b;
      margin: 0;
      font-weight: 400;
    }
    .options-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
      gap: 2rem;
      width: 100%;
      max-width: 900px;
      animation: fadeInUp 0.6s ease-out;
    }
    .option-card {
      position: relative;
      background: #ffffff;
      border-radius: 16px;
      padding: 2.5rem;
      cursor: pointer;
      color: inherit;
      border: 1px solid rgba(226, 232, 240, 0.8);
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      overflow: hidden;
    }
    .card-disabled {
      opacity: 0.65;
      background: #f8fafc;
      cursor: not-allowed;
    }
    .card-glow {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
      background: linear-gradient(90deg, #f26d21, #fbd5c0);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    .option-card:not(.card-disabled):hover {
      transform: translateY(-6px);
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
      border-color: rgba(242, 109, 33, 0.2);
    }
    .option-card:not(.card-disabled):hover .card-glow {
      opacity: 1;
    }
    .icon-wrapper {
      color: #f26d21;
      margin-bottom: 1.5rem;
      padding: 1.25rem;
      background: #fef1eb;
      border: 1px solid rgba(242, 109, 33, 0.1);
      border-radius: 14px;
      display: inline-flex;
      transition: transform 0.3s ease;
    }
    .option-card:not(.card-disabled):hover .icon-wrapper {
      transform: scale(1.05);
    }
    .card-content {
      flex: 1;
      width: 100%;
    }
    .card-title-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 0.5rem;
      margin-bottom: 0.75rem;
    }
    .card-content h2 {
      font-size: 1.5rem;
      color: #1a2b4c;
      margin: 0;
      font-weight: 700;
      letter-spacing: -0.015em;
    }
    .status-tag {
      font-size: 0.75rem;
      font-weight: 600;
      padding: 2px 8px;
      border-radius: 9999px;
    }
    .status-tag-active {
      background: #dcfce7;
      color: #15803d;
    }
    .status-tag-locked {
      background: #fee2e2;
      color: #b91c1c;
    }
    .card-content p {
      color: #64748b;
      margin: 0;
      line-height: 1.6;
      font-size: 1rem;
    }
    .card-arrow {
      margin-top: 1.5rem;
      align-self: flex-end;
      color: #cbd5e1;
      transition: all 0.3s ease;
    }
    .option-card:not(.card-disabled):hover .card-arrow {
      color: #f26d21;
      transform: translateX(6px);
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
      max-width: 440px;
      padding: 1.75rem;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    }
    .modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 1.25rem;
    }
    .modal-header h3 {
      font-size: 1.25rem;
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
    .auth-mode-selector {
      display: flex;
      background: #f1f5f9;
      border-radius: 8px;
      padding: 4px;
      margin-bottom: 1rem;
    }
    .auth-mode-selector button {
      flex: 1;
      padding: 0.45rem 0;
      border: none;
      background: none;
      font-size: 0.85rem;
      font-weight: 600;
      color: #64748b;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
    }
    .auth-mode-selector button.active {
      background: white;
      color: #1a2b4c;
      box-shadow: 0 1px 3px rgba(0,0,0,0.08);
    }
    .modal-tabs {
      display: flex;
      border-bottom: 1px solid #e2e8f0;
      margin-bottom: 1.25rem;
    }
    .modal-tabs button {
      flex: 1;
      padding: 0.5rem 0;
      background: none;
      border: none;
      border-bottom: 2px solid transparent;
      font-weight: 600;
      color: #64748b;
      cursor: pointer;
    }
    .modal-tabs button.active {
      color: #f26d21;
      border-bottom-color: #f26d21;
    }
    .form-label {
      display: block;
      font-size: 0.875rem;
      font-weight: 600;
      color: #334155;
      margin: 0.75rem 0 0.35rem 0;
    }
    .form-input {
      width: 100%;
      box-sizing: border-box;
      padding: 0.65rem 0.85rem;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      font-size: 0.95rem;
    }
    .permissions-preview {
      margin-top: 1rem;
      padding: 0.75rem;
      background: #f8fafc;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
    }
    .preview-title {
      display: block;
      font-size: 0.75rem;
      font-weight: 700;
      text-transform: uppercase;
      color: #64748b;
      margin-bottom: 0.5rem;
    }
    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.85rem;
      color: #334155;
      margin-bottom: 0.35rem;
      cursor: pointer;
    }
    .checkbox-label input {
      accent-color: #f26d21;
    }
    .modal-actions {
      margin-top: 1.5rem;
    }
    .btn-primary {
      width: 100%;
      padding: 0.75rem;
      background: #f26d21;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      font-size: 1rem;
      cursor: pointer;
      transition: background 0.2s;
    }
    .btn-primary:hover {
      background: #d95a16;
    }
    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
    @keyframes fadeInDown {
      from { opacity: 0; transform: translateY(-20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class HomeComponent {
  showAuthModal = signal(false);
  authTab = signal<'login' | 'register'>('login');
  targetIsAdmin = false;
  loading = signal(false);

  authUsername = '';
  authPassword = '';
  regCanMbl = true;
  regCanInvoice = true;

  constructor(
    public auth: AuthService,
    private toast: ToastService,
    private router: Router
  ) {}

  userInitials(): string {
    const name = this.auth.currentUser()?.username || 'U';
    return name.slice(0, 2).toUpperCase();
  }

  openAuthModal(tab: 'login' | 'register', asAdmin = false) {
    this.authTab.set(tab);
    this.targetIsAdmin = asAdmin;
    this.authUsername = '';
    this.authPassword = '';
    this.regCanMbl = true;
    this.regCanInvoice = true;
    this.showAuthModal.set(true);
  }

  closeAuthModal() {
    this.showAuthModal.set(false);
  }

  submitAuth() {
    if (!this.authUsername || !this.authPassword) {
      this.toast.show('Please enter both username and password.', 'error');
      return;
    }

    this.loading.set(true);
    if (this.authTab() === 'login') {
      this.auth.login({
        username: this.authUsername,
        password: this.authPassword,
        as_admin: this.targetIsAdmin
      }).subscribe({
        next: (res) => {
          this.loading.set(false);
          this.toast.show(`Welcome back, ${res.user?.username || this.authUsername}!`, 'success');
          this.closeAuthModal();
          if (res.user?.is_admin && this.targetIsAdmin) {
            this.router.navigate(['/admin']);
          }
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || err.error?.error || 'Invalid credentials.';
          this.toast.show(msg, 'error');
        }
      });
    } else {
      this.auth.register({
        username: this.authUsername,
        password: this.authPassword,
        is_admin: this.targetIsAdmin,
        can_mbl_processing: this.targetIsAdmin ? true : this.regCanMbl,
        can_invoice_processing: this.targetIsAdmin ? true : this.regCanInvoice
      }).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.show('Registration successful! Logging in...', 'success');
          this.authTab.set('login');
          this.submitAuth();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || err.error?.error || 'Registration failed.';
          this.toast.show(msg, 'error');
        }
      });
    }
  }

  logout() {
    this.auth.logout();
    this.toast.show('You have logged out.', 'info');
  }

  navigateToWorkflow(path: string, hasPermission: boolean, name: string) {
    if (!hasPermission) {
      this.toast.show(`Access restricted: You do not have permission for ${name}.`, 'error');
      return;
    }
    this.router.navigate([path]);
  }
}