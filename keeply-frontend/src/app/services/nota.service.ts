import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Nota, NotaRequest } from '../models/nota.model';
import { environment } from '../../environments/environment';

/**
 * Servicio encargado de gestionar las operaciones CRUD de las notas.
 * Se inyecta a nivel raíz para estar disponible en toda la aplicación.
 */
@Injectable({ providedIn: 'root' })
export class NotaService {
    // Cliente HTTP para realizar las peticiones al backend
    private http = inject(HttpClient);
    
    // URL base de la API para el endpoint de notas, obtenida de los entornos
    private apiUrl = `${environment.apiUrl}/api/notas`;

    /**
     * Obtiene todas las notas asociadas a un ítem específico de la biblioteca del usuario.
     * @param itemUsuarioId ID del ítem en la biblioteca.
     */
    getByItemId(itemUsuarioId: number): Observable<Nota[]> {
        return this.http.get<Nota[]>(`${this.apiUrl}/item/${itemUsuarioId}`);
    }

    /**
     * Crea una nueva nota y la guarda en la base de datos.
     * @param nota Objeto con los datos de la nota a crear.
     */
    create(nota: NotaRequest): Observable<Nota> {
        return this.http.post<Nota>(this.apiUrl, nota);
    }

    /**
     * Elimina una nota existente por su ID.
     * @param id ID de la nota a eliminar.
     */
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
