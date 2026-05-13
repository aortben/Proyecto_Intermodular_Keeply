import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, GoogleAuthRequest } from '../models/auth.model';
import { environment } from '../../environments/environment';

/**
 * Servicio centralizado para gestionar la autenticación en el Frontend.
 * Maneja el inicio de sesión, registro, OAuth de Google y la gestión del token JWT.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    // URL del endpoint de autenticación obtenida de las variables de entorno
    private apiUrl = `${environment.apiUrl}/api/auth`;

    // BehaviorSubject para mantener y emitir el estado actual del usuario a toda la aplicación reactivamente
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getStoredUser());
    currentUser$ = this.currentUserSubject.asObservable();

    /**
     * Inicia sesión con credenciales tradicionales.
     * @param request Objeto con nombreUsuario y contrasena.
     * @returns Observable con la respuesta del servidor (incluye token JWT y datos del usuario).
     */
    login(request: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
            tap(response => this.storeUser(response))
        );
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param request Objeto con los datos de registro (usuario, email, contraseña, avatarUrl).
     * @returns Observable con la respuesta del servidor tras iniciar sesión automáticamente.
     */
    register(request: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
            tap(response => this.storeUser(response))
        );
    }

    /**
     * Envía el token de Google al backend para validar y autenticar/registrar al usuario.
     * @param credential Token JWT devuelto por el widget de Google.
     * @returns Observable con los datos del usuario del sistema.
     */
    loginWithGoogle(credential: string): Observable<AuthResponse> {
        const request: GoogleAuthRequest = { credential };
        return this.http.post<AuthResponse>(`${this.apiUrl}/google`, request).pipe(
            tap(response => this.storeUser(response))
        );
    }

    /**
     * Cierra la sesión del usuario eliminando sus datos del almacenamiento local
     * y actualizando el estado reactivo.
     */
    logout(): void {
        localStorage.removeItem('keeply_user');
        this.currentUserSubject.next(null);
    }

    /**
     * Obtiene el token JWT actual almacenado.
     * @returns El token o null si no hay sesión iniciada.
     */
    getToken(): string | null {
        const user = this.getStoredUser();
        return user ? user.token : null;
    }

    /**
     * Obtiene el ID del usuario actualmente logueado.
     */
    getUserId(): number | null {
        const user = this.getStoredUser();
        return user ? user.idUsuario : null;
    }

    /**
     * Comprueba de forma síncrona si hay un usuario logueado basándose en la existencia del token.
     */
    isLoggedIn(): boolean {
        return this.getToken() !== null;
    }

    /**
     * Guarda los datos del usuario (incluyendo el token) en localStorage
     * y notifica a los suscriptores del cambio de estado.
     */
    private storeUser(response: AuthResponse): void {
        localStorage.setItem('keeply_user', JSON.stringify(response));
        this.currentUserSubject.next(response);
    }

    /**
     * Actualiza la URL del avatar en el almacenamiento local sin necesidad de volver a loguearse.
     * Se usa cuando el usuario cambia su foto desde el perfil.
     * @param avatarUrl Nueva URL del avatar.
     */
    updateStoredAvatar(avatarUrl: string): void {
        const user = this.getStoredUser();
        if (user) {
            user.avatarUrl = avatarUrl;
            localStorage.setItem('keeply_user', JSON.stringify(user));
            this.currentUserSubject.next(user);
        }
    }

    /**
     * Actualiza la configuración de banners en el almacenamiento local.
     */
    updateStoredBanners(customBanners: string): void {
        const user = this.getStoredUser();
        if (user) {
            user.customBanners = customBanners;
            localStorage.setItem('keeply_user', JSON.stringify(user));
            this.currentUserSubject.next(user);
        }
    }

    /**
     * Recupera el objeto del usuario almacenado en el navegador (localStorage).
     */
    getStoredUser(): AuthResponse | null {
        const stored = localStorage.getItem('keeply_user');
        return stored ? JSON.parse(stored) : null;
    }
}
