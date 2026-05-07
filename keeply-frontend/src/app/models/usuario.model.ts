export interface Usuario {
  idUsuario?: number;
  nombreUsuario: string;
  email: string;
  contrasenaHash: string;
  fechaRegistro?: string;
  avatarUrl?: string;
}
