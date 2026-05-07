import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContenidoUsuario } from '../models/contenido-usuario.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ContenidoUsuarioService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/contenidos`;

    getAll(): Observable<ContenidoUsuario[]> {
        return this.http.get<ContenidoUsuario[]>(this.apiUrl);
    }

    getById(id: number): Observable<ContenidoUsuario> {
        return this.http.get<ContenidoUsuario>(`${this.apiUrl}/${id}`);
    }

    getByItemUsuarioId(itemUsuarioId: number): Observable<ContenidoUsuario[]> {
        return this.http.get<ContenidoUsuario[]>(`${this.apiUrl}/item/${itemUsuarioId}`);
    }

    create(contenido: ContenidoUsuario): Observable<ContenidoUsuario> {
        return this.http.post<ContenidoUsuario>(this.apiUrl, contenido);
    }

    update(id: number, contenido: ContenidoUsuario): Observable<ContenidoUsuario> {
        return this.http.put<ContenidoUsuario>(`${this.apiUrl}/${id}`, contenido);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
