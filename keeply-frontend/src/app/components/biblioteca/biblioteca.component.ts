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
    imports: [NgFor, RouterLink, NavbarComponent, TranslateModule],
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

    // Imágenes por defecto de cada categoría
    private readonly defaultBanners: Record<string, string> = {
        'anime-manga': 'assets/images/cat-anime.jpg',
        'libros': 'assets/images/cat-libros.jpg',
        'series': 'assets/images/cat-series.jpg',
        'peliculas': 'assets/images/cat-peliculas.jpg',
        'videojuegos': 'assets/images/cat-juegos.jpg'
    };

    // Inicializar sin imagen para evitar el flicker
    categorias = [
        { nombre: 'Anime y Manga', slug: 'anime-manga', imagen: '', ruta: '/biblioteca/anime-manga' },
        { nombre: 'Libros', slug: 'libros', imagen: '', ruta: '/biblioteca/libros' },
        { nombre: 'Series', slug: 'series', imagen: '', ruta: '/biblioteca/series' },
        { nombre: 'Películas', slug: 'peliculas', imagen: '', ruta: '/biblioteca/peliculas' },
        { nombre: 'Videojuegos', slug: 'videojuegos', imagen: '', ruta: '/biblioteca/videojuegos' }
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
            this.loadCustomBanners(currentUser);
        }
    }

    private loadCustomBanners(currentUser: any): void {
        // Primero, intentar usar los banners guardados en el almacenamiento local
        let bannersApplied = false;

        if (currentUser.customBanners) {
            try {
                const banners = JSON.parse(currentUser.customBanners);
                this.categorias.forEach(cat => {
                    if (banners[cat.slug]) {
                        cat.imagen = this.archivoService.getFullUrl(banners[cat.slug]);
                    } else {
                        cat.imagen = this.defaultBanners[cat.slug] || '';
                    }
                });
                bannersApplied = true;
            } catch (e) { /* ignore parse errors */ }
        }

        if (!bannersApplied) {
            // Si no hay datos en el cache local, usar defaults inmediatamente
            this.categorias.forEach(cat => {
                cat.imagen = this.defaultBanners[cat.slug] || '';
            });
        }

        // Luego, verificar con el backend por si hay actualizaciones
        this.usuarioService.getById(currentUser.idUsuario).subscribe({
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
