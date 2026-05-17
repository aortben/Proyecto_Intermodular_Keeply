import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-comunidad',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslateModule, NavbarComponent],
  templateUrl: './comunidad.component.html',
  styleUrls: ['./comunidad.component.scss']
})
export class ComunidadComponent implements OnInit {
  query: string = '';
  resultado: Usuario | null = null;
  mensaje: string = '';
  cargando: boolean = false;
  currentUserId: number | undefined;

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getStoredUser()?.idUsuario;
  }

  buscar() {
    if (!this.query.trim()) {
      return;
    }
    this.cargando = true;
    this.mensaje = '';
    this.resultado = null;

    this.usuarioService.getByNombre(this.query.trim()).subscribe({
      next: (user) => {
        this.resultado = user;
        this.cargando = false;
        if (user.idUsuario === this.currentUserId) {
           this.mensaje = 'Este es tu propio perfil.';
        }
      },
      error: (err) => {
        this.cargando = false;
        if (err.status === 404) {
          this.mensaje = 'Usuario no encontrado.';
        } else {
          this.mensaje = 'Error al buscar usuario.';
        }
      }
    });
  }

  verPerfil(nombreUsuario: string) {
    this.router.navigate(['/usuario', nombreUsuario]);
  }

  getUserAvatarUrl(user: any): string {
    if (!user || !user.avatarUrl) return 'assets/images/default-avatar.png';
    if (user.avatarUrl.startsWith('preset:av')) {
        const presetId = user.avatarUrl.split(':')[1];
        return `assets/images/avatar${presetId.replace('av', '')}.jpg`;
    }
    return user.avatarUrl;
  }
}
