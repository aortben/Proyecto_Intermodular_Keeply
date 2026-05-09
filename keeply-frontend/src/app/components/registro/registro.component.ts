import { Component, inject, AfterViewInit, NgZone, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

declare const google: any;

@Component({
    selector: 'app-registro',
    standalone: true,
    imports: [FormsModule, RouterLink],
    templateUrl: './registro.component.html',
    styleUrl: './registro.component.css'
})
export class RegistroComponent implements AfterViewInit {
    private authService = inject(AuthService);
    private router = inject(Router);
    private ngZone = inject(NgZone);

    @ViewChild('googleBtn', { static: false }) googleBtn!: ElementRef;

    nombreUsuario = '';
    contrasena = '';
    email = '';
    mensaje = '';
    error = '';
    loading = false;

    ngAfterViewInit(): void {
        this.initializeGoogleButton();
    }

    private initializeGoogleButton(): void {
        const interval = setInterval(() => {
            if (typeof google !== 'undefined' && google.accounts) {
                clearInterval(interval);
                google.accounts.id.initialize({
                    client_id: environment.googleClientId,
                    callback: (response: any) => this.handleGoogleCallback(response)
                });
                google.accounts.id.renderButton(
                    this.googleBtn.nativeElement,
                    {
                        type: 'standard',
                        theme: 'filled_black',
                        size: 'large',
                        text: 'signup_with',
                        shape: 'rectangular',
                        width: 320
                    }
                );
            }
        }, 100);

        setTimeout(() => clearInterval(interval), 10000);
    }

    private handleGoogleCallback(response: any): void {
        this.ngZone.run(() => {
            this.loading = true;
            this.error = '';
            this.authService.loginWithGoogle(response.credential).subscribe({
                next: (res) => {
                    this.mensaje = `¡Bienvenido ${res.nombreUsuario}! Redirigiendo...`;
                    setTimeout(() => this.router.navigate(['/biblioteca']), 1500);
                },
                error: (err) => {
                    this.loading = false;
                    this.error = err.error?.error || 'Error al registrarse con Google.';
                }
            });
        });
    }

    registrar(): void {
        this.mensaje = '';
        this.error = '';

        // Validación de email en el frontend
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.email)) {
            this.error = 'El formato del email no es válido.';
            return;
        }

        this.loading = true;

        this.authService.register({
            nombreUsuario: this.nombreUsuario,
            contrasena: this.contrasena,
            email: this.email
        }).subscribe({
            next: (res) => {
                this.mensaje = `¡Bienvenido ${res.nombreUsuario}! Redirigiendo...`;
                setTimeout(() => this.router.navigate(['/biblioteca']), 1500);
            },
            error: (err) => {
                this.loading = false;
                this.error = err.error?.error || 'Error al registrar usuario. Revisa los datos.';
                console.error(err);
            }
        });
    }
}
