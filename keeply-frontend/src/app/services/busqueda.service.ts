import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoBusqueda } from '../models/resultado-busqueda.model';
import { TipoObra } from '../models/obra.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BusquedaService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/busqueda`;

    buscarUnificada(query: string, tipo: TipoObra): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/unificada?query=${encodeURIComponent(query)}&tipo=${tipo}`
        );
    }

    buscarPeliculas(query: string): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/tmdb/peliculas?query=${encodeURIComponent(query)}`
        );
    }

    buscarSeries(query: string): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/tmdb/series?query=${encodeURIComponent(query)}`
        );
    }

    buscarAnimes(query: string): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/jikan/animes?query=${encodeURIComponent(query)}`
        );
    }

    buscarVideojuegos(query: string): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/rawg/videojuegos?query=${encodeURIComponent(query)}`
        );
    }

    buscarLibros(query: string): Observable<ResultadoBusqueda[]> {
        return this.http.get<ResultadoBusqueda[]>(
            `${this.apiUrl}/google/books?query=${encodeURIComponent(query)}`
        );
    }
}
