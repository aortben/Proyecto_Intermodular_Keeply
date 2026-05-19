import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Seguimiento {
  idSeguimiento?: number;
  seguidor?: { idUsuario: number };
  seguido?: { idUsuario: number };
  fechaSeguimiento?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SeguimientoService {
  private apiUrl = `${environment.apiUrl}/api/seguimientos`;

  constructor(private http: HttpClient) {}

  seguir(seguidorId: number, seguidoId: number): Observable<Seguimiento> {
    const seguimiento: Seguimiento = {
      seguidor: { idUsuario: seguidorId },
      seguido: { idUsuario: seguidoId }
    };
    return this.http.post<Seguimiento>(this.apiUrl, seguimiento);
  }

  dejarDeSeguir(idSeguimiento: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idSeguimiento}`);
  }

  getSeguidores(usuarioId: number): Observable<Seguimiento[]> {
    return this.http.get<Seguimiento[]>(`${this.apiUrl}/seguido/${usuarioId}`);
  }

  getSeguidos(usuarioId: number): Observable<Seguimiento[]> {
    return this.http.get<Seguimiento[]>(`${this.apiUrl}/seguidor/${usuarioId}`);
  }
}
