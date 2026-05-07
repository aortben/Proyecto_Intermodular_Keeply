import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/auth`;

    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getStoredUser());
    currentUser$ = this.currentUserSubject.asObservable();

    login(request: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
            tap(response => this.storeUser(response))
        );
    }

    register(request: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
            tap(response => this.storeUser(response))
        );
    }

    logout(): void {
        localStorage.removeItem('keeply_user');
        this.currentUserSubject.next(null);
    }

    getToken(): string | null {
        const user = this.getStoredUser();
        return user ? user.token : null;
    }

    getUserId(): number | null {
        const user = this.getStoredUser();
        return user ? user.idUsuario : null;
    }

    isLoggedIn(): boolean {
        return this.getToken() !== null;
    }

    private storeUser(response: AuthResponse): void {
        localStorage.setItem('keeply_user', JSON.stringify(response));
        this.currentUserSubject.next(response);
    }

    getStoredUser(): AuthResponse | null {
        const stored = localStorage.getItem('keeply_user');
        return stored ? JSON.parse(stored) : null;
    }
}
