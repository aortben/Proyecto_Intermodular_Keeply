import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FileValidationResult {
    valid: boolean;
    error?: string;
}

const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
const ALLOWED_VIDEO_TYPES = ['video/mp4', 'video/webm', 'video/quicktime'];
const ALLOWED_AUDIO_TYPES = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/mp4'];

const MAX_IMAGE_SIZE = 5 * 1024 * 1024;   // 5 MB
const MAX_VIDEO_SIZE = 50 * 1024 * 1024;  // 50 MB
const MAX_AUDIO_SIZE = 10 * 1024 * 1024;  // 10 MB

@Injectable({ providedIn: 'root' })
export class ArchivoService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/archivos`;

    upload(file: File): Observable<{ url: string; filename: string }> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<{ url: string; filename: string }>(`${this.apiUrl}/upload`, formData);
    }

    uploadAvatar(file: File): Observable<{ url: string; filename: string }> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<{ url: string; filename: string }>(`${this.apiUrl}/upload/avatar`, formData);
    }

    /** Convierte una URL relativa del backend a URL absoluta */
    getFullUrl(relativeUrl: string): string {
        if (relativeUrl.startsWith('http')) {
            return relativeUrl;
        }
        return environment.apiUrl + relativeUrl;
    }

    /** Valida un archivo multimedia (imagen, video o audio) en el cliente */
    validateFile(file: File): FileValidationResult {
        const isImage = ALLOWED_IMAGE_TYPES.includes(file.type);
        const isVideo = ALLOWED_VIDEO_TYPES.includes(file.type);
        const isAudio = ALLOWED_AUDIO_TYPES.includes(file.type);

        if (!isImage && !isVideo && !isAudio) {
            return { valid: false, error: 'FILE_ERRORS.INVALID_TYPE' };
        }
        if (isImage && file.size > MAX_IMAGE_SIZE) {
            return { valid: false, error: 'FILE_ERRORS.IMAGE_TOO_LARGE' };
        }
        if (isVideo && file.size > MAX_VIDEO_SIZE) {
            return { valid: false, error: 'FILE_ERRORS.VIDEO_TOO_LARGE' };
        }
        if (isAudio && file.size > MAX_AUDIO_SIZE) {
            return { valid: false, error: 'FILE_ERRORS.AUDIO_TOO_LARGE' };
        }
        return { valid: true };
    }

    /** Valida que un archivo sea exclusivamente una imagen */
    validateImageOnly(file: File): FileValidationResult {
        if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
            return { valid: false, error: 'FILE_ERRORS.IMAGE_ONLY' };
        }
        if (file.size > MAX_IMAGE_SIZE) {
            return { valid: false, error: 'FILE_ERRORS.IMAGE_TOO_LARGE' };
        }
        return { valid: true };
    }
}
