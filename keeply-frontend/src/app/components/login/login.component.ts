import { Component, inject, OnInit, AfterViewInit, NgZone, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

declare const google: any;

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [FormsModule, RouterLink],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, AfterViewInit {
    private authService = inject(AuthService);
    private router = inject(Router);
    private ngZone = inject(NgZone);

    @ViewChild('googleBtn', { static: false }) googleBtn!: ElementRef;

    nombreUsuario = '';
    contrasena = '';
    error = '';
    loading = false;

    ngOnInit(): void {
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/biblioteca']);
        }
    }

    ngAfterViewInit(): void {
        this.initializeGoogleButton();
    }

    private initializeGoogleButton(): void {
        // Esperar a que el script de Google se cargue
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
                        text: 'signin_with',
                        shape: 'rectangular',
                        width: 320
                    }
                );
            }
        }, 100);

        // Timeout de seguridad para no dejar el interval eternamente
        setTimeout(() => clearInterval(interval), 10000);
    }

    private handleGoogleCallback(response: any): void {
        this.ngZone.run(() => {
            this.loading = true;
            this.error = '';
            this.authService.loginWithGoogle(response.credential).subscribe({
                next: () => {
                    this.router.navigate(['/biblioteca']);
                },
                error: (err) => {
                    this.loading = false;
                    this.error = err.error?.error || 'Error al iniciar sesión con Google.';
                }
            });
        });
    }

    login(): void {
        this.error = '';
        this.loading = true;
        this.authService.login({ nombreUsuario: this.nombreUsuario, contrasena: this.contrasena }).subscribe({
            next: () => {
                this.router.navigate(['/biblioteca']);
            },
            error: () => {
                this.loading = false;
                this.error = 'Usuario o contraseña incorrectos.';
            }
        });
    }
}
