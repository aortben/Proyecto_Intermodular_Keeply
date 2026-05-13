import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { ArchivoService } from '../../services/archivo.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
    selector: 'app-biblioteca',
    standalone: true,
    imports: [NgFor, NgIf, RouterLink, NavbarComponent, TranslateModule],
    templateUrl: './biblioteca.component.html',
    styleUrls: ['./biblioteca.component.scss']
})
export class BibliotecaComponent implements OnInit {
    private authService = inject(AuthService);
    private archivoService = inject(ArchivoService);
    private usuarioService = inject(UsuarioService);
    private router = inject(Router);
    private translate = inject(TranslateService);

    usuario = {
        nombre: '',
        avatarUrl: ''
    };

    categorias = [
        { nombre: 'Anime y Manga', slug: 'anime-manga', imagen: 'assets/images/cat-anime.jpg', ruta: '/biblioteca/anime-manga' },
        { nombre: 'Libros', slug: 'libros', imagen: 'assets/images/cat-libros.jpg', ruta: '/biblioteca/libros' },
        { nombre: 'Series', slug: 'series', imagen: 'assets/images/cat-series.jpg', ruta: '/biblioteca/series' },
        { nombre: 'Películas', slug: 'peliculas', imagen: 'assets/images/cat-peliculas.jpg', ruta: '/biblioteca/peliculas' },
        { nombre: 'Videojuegos', slug: 'videojuegos', imagen: 'assets/images/cat-juegos.jpg', ruta: '/biblioteca/videojuegos' }
    ];

    ngOnInit(): void {
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['/']);
            return;
        }

        const currentUser = this.authService.getStoredUser();
        if (currentUser) {
            this.usuario.nombre = currentUser.nombreUsuario;
            this.usuario.avatarUrl = currentUser.avatarUrl || '';
            this.loadCustomBanners(currentUser.idUsuario);
        }
    }

    private loadCustomBanners(userId: number): void {
        this.usuarioService.getById(userId).subscribe({
            next: (user: any) => {
                if (user.customBanners) {
                    try {
                        const banners = JSON.parse(user.customBanners);
                        this.categorias.forEach(cat => {
                            if (banners[cat.slug]) {
                                cat.imagen = this.archivoService.getFullUrl(banners[cat.slug]);
                            }
                        });
                    } catch (e) { /* ignore parse errors */ }
                }
            }
        });
    }

    }
}
