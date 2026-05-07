import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [FormsModule, RouterLink],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    nombreUsuario = '';
    contrasena = '';
    error = '';

    login(): void {
        this.error = '';
        this.authService.login({ nombreUsuario: this.nombreUsuario, contrasena: this.contrasena }).subscribe({
            next: () => {
                this.router.navigate(['/biblioteca']);
            },
            error: () => {
                this.error = 'Usuario o contraseña incorrectos.';
            }
        });
    }
}
