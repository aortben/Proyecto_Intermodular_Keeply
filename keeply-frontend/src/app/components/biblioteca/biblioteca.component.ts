import { Component, OnInit } from '@angular/core';
import { NgFor } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-biblioteca',
    standalone: true,
    imports: [NgFor, RouterLink, NavbarComponent],
    templateUrl: './biblioteca.component.html',
    styleUrls: ['./biblioteca.component.scss']
})
export class BibliotecaComponent implements OnInit {

    // Datos del usuario real
    usuario = {
        nombre: '',
        avatarUrl: 'assets/images/avatar-peter.png'
    };

    // Lista de categorías para generar la cuadrícula
    categorias = [
        { nombre: 'Anime y Manga', imagen: 'assets/images/cat-anime.jpg', ruta: '/biblioteca/anime-manga' },
        { nombre: 'Libros', imagen: 'assets/images/cat-libros.jpg', ruta: '/biblioteca/libros' },
        { nombre: 'Series', imagen: 'assets/images/cat-series.jpg', ruta: '/biblioteca/series' },
        { nombre: 'Películas', imagen: 'assets/images/cat-peliculas.jpg', ruta: '/biblioteca/peliculas' },
        { nombre: 'Videojuegos', imagen: 'assets/images/cat-juegos.jpg', ruta: '/biblioteca/videojuegos' }
    ];

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        // Verificar que el usuario esté logueado
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['/']);
            return;
        }

        // Obtener nombre real del usuario logueado
        const currentUser = this.authService.getStoredUser();
        if (currentUser) {
            this.usuario.nombre = currentUser.nombreUsuario;
        }
    }
}
