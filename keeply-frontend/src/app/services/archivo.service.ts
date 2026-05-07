import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ArchivoService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/archivos`;

    upload(file: File): Observable<{ url: string; filename: string }> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<{ url: string; filename: string }>(`${this.apiUrl}/upload`, formData);
    }

    /** Convierte una URL relativa del backend a URL absoluta */
    getFullUrl(relativeUrl: string): string {
        if (relativeUrl.startsWith('http')) {
            return relativeUrl;
        }
        return environment.apiUrl + relativeUrl;
    }
}
