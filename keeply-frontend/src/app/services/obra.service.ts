import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Obra, TipoObra } from '../models/obra.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ObraService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/obras`;

    getAll(): Observable<Obra[]> {
        return this.http.get<Obra[]>(this.apiUrl);
    }

    getById(id: number): Observable<Obra> {
        return this.http.get<Obra>(`${this.apiUrl}/${id}`);
    }

    getByTipo(tipo: TipoObra): Observable<Obra[]> {
        return this.http.get<Obra[]>(`${this.apiUrl}?tipo=${tipo}`);
    }

    searchByTitulo(titulo: string): Observable<Obra[]> {
        return this.http.get<Obra[]>(`${this.apiUrl}?titulo=${titulo}`);
    }

    create(obra: Obra): Observable<Obra> {
        return this.http.post<Obra>(this.apiUrl, obra);
    }

    update(id: number, obra: Obra): Observable<Obra> {
        return this.http.put<Obra>(`${this.apiUrl}/${id}`, obra);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
