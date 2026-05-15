import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Seguimiento {
  idSeguimiento?: number;
  idUsuarioSeguidor: number;
  idUsuarioSeguido: number;
  fechaSeguimiento?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SeguimientoService {
  private apiUrl = `${environment.apiUrl}/seguimientos`;

  constructor(private http: HttpClient) {}

  seguir(seguidorId: number, seguidoId: number): Observable<Seguimiento> {
    const seguimiento: Seguimiento = {
      idUsuarioSeguidor: seguidorId,
      idUsuarioSeguido: seguidoId
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
