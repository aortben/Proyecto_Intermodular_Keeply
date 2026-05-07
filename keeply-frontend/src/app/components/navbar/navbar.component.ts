import { Component, inject, HostListener } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [RouterLink, AsyncPipe, NgIf, FormsModule],
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    currentUser$ = this.authService.currentUser$;

    // Control de qué menú está abierto
    menuAbierto: string | null = null;

    // Toggle login ↔ registro dentro del popup
    modoAuth: 'login' | 'registro' = 'login';

    // Campos de login
    loginNombreUsuario = '';
    loginPassword = '';

    // Campos de registro
    regNombreUsuario = '';
    regPassword = '';

    // Errores y mensajes
    authError = '';
    authExito = '';

    toggleMenu(menu: string, event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.menuAbierto = this.menuAbierto === menu ? null : menu;
        this.authError = '';
        this.authExito = '';
    }

    @HostListener('document:click')
    cerrarMenus(): void {
        this.menuAbierto = null;
    }

    onPopoverClick(event: Event): void {
        event.stopPropagation();
    }

    toggleModoAuth(event?: Event): void {
        if (event) event.preventDefault();
        this.modoAuth = this.modoAuth === 'login' ? 'registro' : 'login';
        this.authError = '';
        this.authExito = '';
    }

    loginInline(): void {
        if (!this.loginNombreUsuario || !this.loginPassword) {
            this.authError = 'Completa todos los campos';
            return;
        }
        this.authService.login({ nombreUsuario: this.loginNombreUsuario, contrasena: this.loginPassword }).subscribe({
            next: () => {
                this.menuAbierto = null;
                this.limpiarCampos();
            },
            error: () => this.authError = 'Usuario o contraseña incorrectos'
        });
    }

    registroInline(): void {
        if (!this.regNombreUsuario || !this.regPassword) {
            this.authError = 'Completa todos los campos';
            return;
        }
        this.authService.register({
            nombreUsuario: this.regNombreUsuario,
            contrasena: this.regPassword
        }).subscribe({
            next: () => {
                this.menuAbierto = null;
                this.limpiarCampos();
            },
            error: () => this.authError = 'No se pudo crear la cuenta. ¿Ya existe ese usuario?'
        });
    }

    logout(): void {
        this.authService.logout();
        this.menuAbierto = null;
        this.router.navigate(['/']);
    }

    irABiblioteca(): void {
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/biblioteca']);
        } else {
            // Abrir el popup de cuenta para que inicie sesión
            this.menuAbierto = 'cuenta';
            this.modoAuth = 'login';
            this.authError = '';
            this.authExito = '';
        }
    }

    private limpiarCampos(): void {
        this.loginNombreUsuario = '';
        this.loginPassword = '';
        this.regNombreUsuario = '';
        this.regPassword = '';
        this.authError = '';
        this.authExito = '';
        this.modoAuth = 'login';
    }
}