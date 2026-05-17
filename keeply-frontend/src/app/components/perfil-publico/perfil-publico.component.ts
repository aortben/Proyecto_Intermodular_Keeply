import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { UsuarioService } from '../../services/usuario.service';
import { ItemUsuarioService } from '../../services/item-usuario.service';
import { SeguimientoService } from '../../services/seguimiento.service';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-perfil-publico',
  standalone: true,
  imports: [CommonModule, TranslateModule, NavbarComponent],
  templateUrl: './perfil-publico.component.html',
  styleUrls: ['./perfil-publico.component.scss']
})
export class PerfilPublicoComponent implements OnInit {
  nombreUsuario: string = '';
  usuario: Usuario | null = null;
  items: any[] = [];
  
  cargando: boolean = true;
  error: boolean = false;

  // Estado social
  miId: number | undefined;
  idSeguimiento: number | null = null;
  siguiendo: boolean = false;
  numeroSeguidores: number = 0;
  numeroSiguiendo: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: UsuarioService,
    private itemUsuarioService: ItemUsuarioService,
    private seguimientoService: SeguimientoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.miId = this.authService.getStoredUser()?.idUsuario;

    this.route.paramMap.subscribe(params => {
      const nombre = params.get('nombreUsuario');
      if (nombre) {
        this.nombreUsuario = nombre;
        this.cargarPerfil();
      } else {
        this.router.navigate(['/comunidad']);
      }
    });
  }

  cargarPerfil() {
    this.cargando = true;
    this.error = false;

    this.usuarioService.getByNombre(this.nombreUsuario).subscribe({
      next: (user) => {
        this.usuario = user;
        this.cargarEstadisticasSociales(user.idUsuario!);
        this.cargarBiblioteca(user.idUsuario!);
      },
      error: () => {
        this.cargando = false;
        this.error = true;
      }
    });
  }

  cargarEstadisticasSociales(perfilId: number) {
    // Ver si le sigo
    if (this.miId && this.miId !== perfilId) {
      this.seguimientoService.getSeguidos(this.miId).subscribe(seguidos => {
        const seg = seguidos.find(s => s.idUsuarioSeguido === perfilId);
        if (seg) {
          this.siguiendo = true;
          this.idSeguimiento = seg.idSeguimiento!;
        } else {
          this.siguiendo = false;
          this.idSeguimiento = null;
        }
      });
    }

    // Contadores
    this.seguimientoService.getSeguidores(perfilId).subscribe(seguidores => {
      this.numeroSeguidores = seguidores.length;
    });
    this.seguimientoService.getSeguidos(perfilId).subscribe(seguidos => {
      this.numeroSiguiendo = seguidos.length;
    });
  }

  cargarBiblioteca(perfilId: number) {
    this.itemUsuarioService.getByUsuarioId(perfilId).subscribe({
      next: (items) => {
        this.items = items;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  toggleSeguir() {
    if (!this.miId || !this.usuario?.idUsuario) return;

    if (this.siguiendo && this.idSeguimiento) {
      this.seguimientoService.dejarDeSeguir(this.idSeguimiento).subscribe(() => {
        this.siguiendo = false;
        this.idSeguimiento = null;
        this.numeroSeguidores--;
      });
    } else {
      this.seguimientoService.seguir(this.miId, this.usuario.idUsuario).subscribe(nuevoSeg => {
        this.siguiendo = true;
        this.idSeguimiento = nuevoSeg.idSeguimiento!;
        this.numeroSeguidores++;
      });
    }
  }

  get isMiPerfil(): boolean {
    return this.miId === this.usuario?.idUsuario;
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
