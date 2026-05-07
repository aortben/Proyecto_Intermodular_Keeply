import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ItemUsuario } from '../models/item-usuario.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ItemUsuarioService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/items`;

    getAll(): Observable<ItemUsuario[]> {
        return this.http.get<ItemUsuario[]>(this.apiUrl);
    }

    getById(id: number): Observable<ItemUsuario> {
        return this.http.get<ItemUsuario>(`${this.apiUrl}/${id}`);
    }

    getByUsuarioId(usuarioId: number): Observable<ItemUsuario[]> {
        return this.http.get<ItemUsuario[]>(`${this.apiUrl}/usuario/${usuarioId}`);
    }

    create(item: ItemUsuario): Observable<ItemUsuario> {
        return this.http.post<ItemUsuario>(this.apiUrl, item);
    }

    update(id: number, item: ItemUsuario): Observable<ItemUsuario> {
        return this.http.put<ItemUsuario>(`${this.apiUrl}/${id}`, item);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
