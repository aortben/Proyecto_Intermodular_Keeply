export interface AuthResponse {
    token: string;
    idUsuario: number;
    nombreUsuario: string;
}

export interface LoginRequest {
    nombreUsuario: string;
    contrasena: string;
}

export interface RegisterRequest {
    nombreUsuario: string;
    contrasena: string;
}
