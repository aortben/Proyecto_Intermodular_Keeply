import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Nota, NotaRequest } from '../models/nota.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotaService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/notas`;

    getByItemId(itemUsuarioId: number): Observable<Nota[]> {
        return this.http.get<Nota[]>(`${this.apiUrl}/item/${itemUsuarioId}`);
    }

    create(nota: NotaRequest): Observable<Nota> {
        return this.http.post<Nota>(this.apiUrl, nota);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
