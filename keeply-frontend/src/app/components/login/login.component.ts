import { Component, inject, AfterViewInit, ViewChild, ElementRef, NgZone } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { environment } from '../../../environments/environment';

declare const google: any;

/**
 * Componente para la página de inicio de sesión.
 * Permite al usuario acceder con sus credenciales o mediante Google OAuth.
 */
@Component({
    selector: 'app-login',
    standalone: true,
    imports: [FormsModule, NgIf, RouterLink, TranslateModule, NavbarComponent],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements AfterViewInit {
    private authService = inject(AuthService);
    private router = inject(Router);
    private ngZone = inject(NgZone);
    private translate = inject(TranslateService);

    // Referencia al contenedor HTML donde se inyectará el botón de Google
    @ViewChild('googleBtnLogin', { static: false }) googleBtnLogin!: ElementRef;

    // Variables enlazadas al formulario (ngModels)
    nombreUsuario = '';
    contrasena = '';
    
    // Mensajes de error y estado de carga para mostrar retroalimentación en la UI
    authError = '';
    cargando = false;

    /**
     * Hook de ciclo de vida que se ejecuta cuando la vista ya está renderizada.
     * Es el momento ideal para inyectar el botón de Google.
     */
    ngAfterViewInit(): void {
        this.initGoogle();
    }

    /**
     * Inicializa la librería de Google Identity Services de forma asíncrona.
     * Busca en intervalos si la variable global 'google' ya ha sido cargada en el DOM.
     */
    private initGoogle(): void {
        const interval = setInterval(() => {
            if (typeof google !== 'undefined' && google.accounts) {
                clearInterval(interval);
                // Inicializa el cliente de Google con el ID y el callback de respuesta
                google.accounts.id.initialize({
                    client_id: environment.googleClientId,
                    callback: (resp: any) => this.handleGoogle(resp)
                });
                // Renderiza visualmente el botón en el contenedor especificado
                if (this.googleBtnLogin?.nativeElement) {
                    google.accounts.id.renderButton(this.googleBtnLogin.nativeElement, {
                        type: 'standard', theme: 'filled_black', size: 'large',
                        text: 'signin_with', shape: 'pill', width: 300
                    });
                }
            }
        }, 200);
        // Timeout de seguridad: detiene la búsqueda si Google no carga en 10 segundos
        setTimeout(() => clearInterval(interval), 10000);
    }

    /**
     * Callback ejecutado automáticamente cuando el usuario completa el login de Google.
     * @param response Objeto devuelto por Google que contiene el token JWT de credencial.
     */
    private handleGoogle(response: any): void {
        // ngZone.run asegura que Angular detecte los cambios de variables (como cargando o authError)
        // ya que la librería de Google opera fuera del contexto de Angular.
        this.ngZone.run(() => {
            this.authError = '';
            this.cargando = true;
            this.authService.loginWithGoogle(response.credential).subscribe({
                next: () => { 
                    this.cargando = false; 
                    this.router.navigate(['/biblioteca']); 
                },
                error: (err) => { 
                    this.cargando = false; 
                    this.authError = err.error?.error || this.translate.instant('ERRORS.GOOGLE_ERROR'); 
                }
            });
        });
    }

    /**
     * Maneja el inicio de sesión tradicional al pulsar el botón "Entrar".
     * Valida que los campos no estén vacíos antes de llamar al backend.
     */
    login(): void {
        if (!this.nombreUsuario || !this.contrasena) {
            this.authError = this.translate.instant('ERRORS.FILL_ALL_FIELDS');
            return;
        }
        
        this.cargando = true;
        this.authService.login({ nombreUsuario: this.nombreUsuario, contrasena: this.contrasena }).subscribe({
            next: () => { 
                this.cargando = false; 
                this.router.navigate(['/biblioteca']); 
            },
            error: () => { 
                this.cargando = false; 
                // Error genérico por seguridad (no especificamos si falló el usuario o la contraseña)
                this.authError = this.translate.instant('ERRORS.INVALID_CREDENTIALS'); 
            }
        });
    }
}
