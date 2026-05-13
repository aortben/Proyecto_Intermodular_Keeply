export interface AuthResponse {
    token: string;
    idUsuario: number;
    nombreUsuario: string;
    email: string;
    avatarUrl?: string;
    customBanners?: string;
}

export interface LoginRequest {
    nombreUsuario: string;
    contrasena: string;
}

export interface RegisterRequest {
    nombreUsuario: string;
    contrasena: string;
    email: string;
    avatarUrl?: string;
}

export interface GoogleAuthRequest {
    credential: string;
}
