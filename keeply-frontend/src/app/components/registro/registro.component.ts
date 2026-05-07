import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-registro',
    standalone: true,
    imports: [FormsModule, RouterLink],
    templateUrl: './registro.component.html',
    styleUrl: './registro.component.css'
})
export class RegistroComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    nombreUsuario = '';
    contrasena = '';
    mensaje = '';
    error = '';

    registrar(): void {
        this.mensaje = '';
        this.error = '';

        this.authService.register({
            nombreUsuario: this.nombreUsuario,
            contrasena: this.contrasena
        }).subscribe({
            next: (res) => {
                this.mensaje = `¡Bienvenido ${res.nombreUsuario}! Redirigiendo...`;
                setTimeout(() => this.router.navigate(['/biblioteca']), 1500);
            },
            error: (err) => {
                this.error = err.error?.error || 'Error al registrar usuario. Revisa los datos.';
                console.error(err);
            }
        });
    }
}
