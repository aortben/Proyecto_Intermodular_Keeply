import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/usuarios`;

    getAll(): Observable<Usuario[]> {
        return this.http.get<Usuario[]>(this.apiUrl);
    }

    getById(id: number): Observable<Usuario> {
        return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
    }

    getByNombre(nombre: string): Observable<Usuario> {
        return this.http.get<Usuario>(`${this.apiUrl}/nombre/${nombre}`);
    }

    create(usuario: Usuario): Observable<Usuario> {
        return this.http.post<Usuario>(this.apiUrl, usuario);
    }

    update(id: number, usuario: Usuario): Observable<Usuario> {
        return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    updateAvatar(id: number, avatarUrl: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}/avatar`, { avatarUrl });
    }

    updateBanners(id: number, customBanners: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}/banners`, { customBanners });
    }
}
