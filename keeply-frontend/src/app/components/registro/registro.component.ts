import { Component, inject, AfterViewInit, ViewChild, ElementRef, NgZone } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';
import { ArchivoService } from '../../services/archivo.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { environment } from '../../../environments/environment';

declare const google: any;

interface AvatarOption {
    id: string;
    url: string;
}

/**
 * Componente para la página de registro.
 * Permite la creación de nuevas cuentas, selección o subida de avatar, y registro vía Google.
 */
@Component({
    selector: 'app-registro',
    standalone: true,
    imports: [FormsModule, NgIf, NgFor, RouterLink, TranslateModule, NavbarComponent],
    templateUrl: './registro.component.html',
    styleUrls: ['./registro.component.scss']
})
export class RegistroComponent implements AfterViewInit {
    private authService = inject(AuthService);
    private archivoService = inject(ArchivoService);
    private router = inject(Router);
    private ngZone = inject(NgZone);
    private translate = inject(TranslateService);

    // Referencia al contenedor HTML para el botón de Google
    @ViewChild('googleBtnRegistro', { static: false }) googleBtnRegistro!: ElementRef;

    // Datos del formulario
    nombreUsuario = '';
    email = '';
    contrasena = '';
    authError = '';
    cargando = false;

    // Gestión del avatar
    selectedAvatarId = 'av1';
    customAvatarUrl: string | null = null;
    subiendoAvatar = false;

    // Lista de avatares predefinidos
    avatarOptions: AvatarOption[] = [
        { id: 'av1', url: 'assets/images/avatar1.jpg' },
        { id: 'av2', url: 'assets/images/avatar2.jpg' },
        { id: 'av3', url: 'assets/images/avatar3.jpg' },
        { id: 'av4', url: 'assets/images/avatar4.jpg' },
        { id: 'av5', url: 'assets/images/avatar5.jpg' }
    ];

    /**
     * Hook que se ejecuta tras renderizar la vista. Inicializa el widget de Google.
     */
    ngAfterViewInit(): void {
        this.initGoogle();
    }

    /**
     * Inicializa la librería de autenticación de Google y renderiza el botón.
     */
    private initGoogle(): void {
        const interval = setInterval(() => {
            if (typeof google !== 'undefined' && google.accounts) {
                clearInterval(interval);
                google.accounts.id.initialize({
                    client_id: environment.googleClientId,
                    callback: (resp: any) => this.handleGoogle(resp)
                });
                if (this.googleBtnRegistro?.nativeElement) {
                    google.accounts.id.renderButton(this.googleBtnRegistro.nativeElement, {
                        type: 'standard', theme: 'filled_black', size: 'large',
                        text: 'signup_with', shape: 'pill', width: 300
                    });
                }
            }
        }, 200);
        setTimeout(() => clearInterval(interval), 10000);
    }

    /**
     * Procesa la respuesta de Google tras un login exitoso en su popup.
     */
    private handleGoogle(response: any): void {
        this.ngZone.run(() => {
            this.authError = '';
            this.cargando = true;
            this.authService.loginWithGoogle(response.credential).subscribe({
                next: () => { this.cargando = false; this.router.navigate(['/biblioteca']); },
                error: (err) => { this.cargando = false; this.authError = err.error?.error || this.translate.instant('ERRORS.GOOGLE_ERROR'); }
            });
        });
    }

    /**
     * Selecciona uno de los avatares predefinidos de la lista.
     * @param av El avatar seleccionado.
     */
    selectAvatar(av: AvatarOption): void {
        this.selectedAvatarId = av.id;
        this.customAvatarUrl = null; // Reinicia el avatar personalizado si se elige uno por defecto
    }

    /**
     * Maneja el evento de selección de un archivo desde el ordenador para usarlo como avatar.
     * Aplica validación local y lo sube al backend.
     */
    onAvatarFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (!input.files || input.files.length === 0) return;

        const file = input.files[0];
        // Valida que sea imagen en el lado del cliente
        const validation = this.archivoService.validateImageOnly(file);
        if (!validation.valid) {
            this.authError = this.translate.instant(validation.error!);
            input.value = '';
            return;
        }

        this.subiendoAvatar = true;
        // Sube el archivo al servidor a través del endpoint específico para avatares
        this.archivoService.uploadAvatar(file).subscribe({
            next: (res) => {
                this.customAvatarUrl = this.archivoService.getFullUrl(res.url);
                this.selectedAvatarId = 'custom';
                this.subiendoAvatar = false;
                input.value = '';
            },
            error: () => {
                this.authError = this.translate.instant('FILE_ERRORS.UPLOAD_FAILED');
                this.subiendoAvatar = false;
                input.value = '';
            }
        });
    }

    /**
     * Devuelve la URL del avatar personalizado si existe, o la del avatar predefinido seleccionado.
     */
    getSelectedAvatarUrl(): string | null {
        if (this.customAvatarUrl) return this.customAvatarUrl;
        const selected = this.getSelectedAvatar();
        return selected ? selected.url : null;
    }

    /**
     * Devuelve el objeto AvatarOption seleccionado actualmente (si es uno predefinido).
     */
    getSelectedAvatar(): AvatarOption | null {
        return this.avatarOptions.find(a => a.id === this.selectedAvatarId) || null;
    }

    /**
     * Realiza validaciones del formulario y envía los datos de registro al servidor.
     */
    registrar(): void {
        // Validación de campos vacíos
        if (!this.nombreUsuario || !this.contrasena || !this.email) {
            this.authError = this.translate.instant('ERRORS.FILL_ALL_FIELDS');
            return;
        }
        // Validación de longitud de contraseña
        if (this.contrasena.length < 6) {
            this.authError = this.translate.instant('REGISTER.PASSWORD_TOO_SHORT');
            return;
        }
        // Validación de formato de email usando expresiones regulares
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.email)) {
            this.authError = this.translate.instant('ERRORS.INVALID_EMAIL');
            return;
        }

        this.cargando = true;
        // Llama al servicio con los datos y la URL del avatar (predefinido o personalizado)
        this.authService.register({
            nombreUsuario: this.nombreUsuario,
            contrasena: this.contrasena,
            email: this.email,
            avatarUrl: this.getSelectedAvatarUrl() || undefined
        }).subscribe({
            next: () => { 
                this.cargando = false; 
                this.router.navigate(['/biblioteca']); 
            },
            error: (err) => {
                this.cargando = false;
                this.authError = err.error?.error || this.translate.instant('ERRORS.REGISTER_FAILED');
            }
        });
    }
}
