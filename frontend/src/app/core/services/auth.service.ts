import { Injectable, signal, computed } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { BackendApiService } from './backend-api.service';
import { UserProfile, UserLoginRequest, UserRegisterRequest, TokenResponse, UpdatePermissionsRequest } from '../models/schemas';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSignal = signal<UserProfile | null>(null);
  public currentUser = computed(() => this.currentUserSignal());
  public isLoggedIn = computed(() => !!this.currentUserSignal());
  public canProcessInvoices = computed(() => {
    const user = this.currentUserSignal();
    return user ? (user.is_admin || user.can_invoice_processing) : true;
  });
  public canProcessMbl = computed(() => {
    const user = this.currentUserSignal();
    return user ? (user.is_admin || user.can_mbl_processing) : true;
  });

  constructor(private backendApi: BackendApiService) {
    this.initFromStorage();
  }

  private initFromStorage(): void {
    const storedUser = localStorage.getItem('user_profile');
    const token = localStorage.getItem('token');
    if (storedUser && token) {
      try {
        this.currentUserSignal.set(JSON.parse(storedUser));
      } catch (e) {
        this.logout();
      }
    }
  }

  login(request: UserLoginRequest): Observable<TokenResponse> {
    return this.backendApi.login(request).pipe(
      tap(response => {
        localStorage.setItem('token', response.access_token);
        if (response.user) {
          localStorage.setItem('user_profile', JSON.stringify(response.user));
          this.currentUserSignal.set(response.user);
        } else {
          // Fetch current profile if not included directly in response
          this.fetchCurrentProfile().subscribe();
        }
      })
    );
  }

  register(request: UserRegisterRequest): Observable<UserProfile> {
    return this.backendApi.register(request);
  }

  fetchCurrentProfile(): Observable<UserProfile> {
    return this.backendApi.getMe().pipe(
      tap(profile => {
        localStorage.setItem('user_profile', JSON.stringify(profile));
        this.currentUserSignal.set(profile);
      })
    );
  }

  updateUserPermissions(username: string, request: UpdatePermissionsRequest): Observable<UserProfile> {
    return this.backendApi.updatePermissions(username, request).pipe(
      tap(updated => {
        if (this.currentUserSignal()?.username === username) {
          localStorage.setItem('user_profile', JSON.stringify(updated));
          this.currentUserSignal.set(updated);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_profile');
    this.currentUserSignal.set(null);
  }
}
