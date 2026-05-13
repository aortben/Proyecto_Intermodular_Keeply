import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, NgIf, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { ItemUsuarioService } from '../../services/item-usuario.service';
import { AuthService } from '../../services/auth.service';
import { ArchivoService } from '../../services/archivo.service';
import { UsuarioService } from '../../services/usuario.service';
import { TipoObra } from '../../models/obra.model';

@Component({
    selector: 'app-categoria-detalle',
    standalone: true,
    imports: [NgFor, NgIf, NgClass, FormsModule, RouterLink, NavbarComponent, TranslateModule],
    templateUrl: './categoria-detalle.component.html',
    styleUrls: ['./categoria-detalle.component.scss']
})
export class CategoriaDetalleComponent implements OnInit {

    categoria = '';
    usuario = '';
    bannerUrl = '';
    searchTerm = '';

    // Modo edición
    modoEdicion = false;
    seleccionados = new Set<number>();

    // Mapeo de categorías a sus datos (banner, nombre visible, etc.)
    private categoriasMap: Record<string, { nombre: string; banner: string; tipos: TipoObra[] }> = {
        'anime-manga': { nombre: 'Anime y Manga', banner: 'assets/images/banner-anime.jpg', tipos: ['ANIME', 'MANGA'] },
        libros: { nombre: 'Libros', banner: 'assets/images/banner-libros.jpg', tipos: ['LIBRO', 'COMIC'] },
        series: { nombre: 'Series', banner: 'assets/images/banner-series.jpg', tipos: ['SERIE'] },
        peliculas: { nombre: 'Películas', banner: 'assets/images/banner-peliculas.jpg', tipos: ['PELICULA'] },
        videojuegos: { nombre: 'Videojuegos', banner: 'assets/images/banner-videojuegos.jpg', tipos: ['VIDEOJUEGO'] }
    };

    // Items reales del usuario filtrados por categoría
    items: { idItemUsuario: number; titulo: string; imagenUrl: string; estado: string }[] = [];
    // Copia completa para filtrar
    todosLosItems: { idItemUsuario: number; titulo: string; imagenUrl: string; estado: string }[] = [];

    // Placeholder por defecto si la API no devuelve imagen
    private readonly placeholderImg = 'assets/images/placeholder-item.jpg';

    // Slug actual para recargar
    private currentSlug = '';

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private itemUsuarioService: ItemUsuarioService,
        private authService: AuthService,
        private archivoService: ArchivoService,
        private usuarioService: UsuarioService
    ) { }

    ngOnInit(): void {
        // Verificar que el usuario esté logueado
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['/']);
            return;
        }

        const userId = this.authService.getUserId();
        if (!userId) {
            this.router.navigate(['/']);
            return;
        }

        // Obtener el nombre del usuario logueado
        const currentUser = this.authService.getStoredUser();
        this.usuario = currentUser?.nombreUsuario || '';

        // Leemos el parámetro :categoria de la URL
        this.route.paramMap.subscribe(params => {
            const slug = params.get('categoria') || '';
            this.currentSlug = slug;
            const config = this.categoriasMap[slug];

            if (config) {
                this.categoria = config.nombre;
                
                // Comprobar si el usuario tiene un banner personalizado para esta categoría
                let customUrl = null;
                if (currentUser && currentUser.customBanners) {
                    try {
                        const bannersObj = JSON.parse(currentUser.customBanners);
                        if (bannersObj[slug]) {
                            customUrl = this.archivoService.getFullUrl(bannersObj[slug]);
                        }
                    } catch (e) {
                        console.error('Error parseando customBanners JSON:', e);
                    }
                }
                
                this.bannerUrl = customUrl ? customUrl : config.banner;
            } else {
                // Si la categoría no existe, volver a biblioteca
                this.router.navigate(['/biblioteca']);
                return;
            }

            this.cargarItems(userId, config.tipos);
        });
    }

    private cargarItems(userId: number, tipos: TipoObra[]): void {
        this.itemUsuarioService.getByUsuarioId(userId).subscribe({
            next: (items) => {
                this.todosLosItems = items
                    .filter(item => tipos.includes(item.obra.tipoObra))
                    .map(item => ({
                        idItemUsuario: item.idItemUsuario!,
                        titulo: item.obra.titulo,
                        imagenUrl: item.obra.urlImagenPrincipal || this.placeholderImg,
                        estado: item.estado
                    }));
                this.filtrarItems();
            },
            error: (err) => {
                console.error('Error cargando items del usuario:', err);
                this.items = [];
                this.todosLosItems = [];
            }
        });
    }

    /** Filtra los items según el término de búsqueda */
    filtrarItems(): void {
        if (!this.searchTerm.trim()) {
            this.items = [...this.todosLosItems];
        } else {
            const term = this.searchTerm.toLowerCase().trim();
            this.items = this.todosLosItems.filter(item =>
                item.titulo.toLowerCase().includes(term)
            );
        }
    }

    /** Activa o desactiva el modo edición */
    toggleModoEdicion(): void {
        this.modoEdicion = !this.modoEdicion;
        if (!this.modoEdicion) {
            this.seleccionados.clear();
        }
    }

    /** Selecciona o deselecciona un item */
    toggleSeleccion(id: number, event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        if (this.seleccionados.has(id)) {
            this.seleccionados.delete(id);
        } else {
            this.seleccionados.add(id);
        }
    }

    /** Comprueba si un item está seleccionado */
    isSeleccionado(id: number): boolean {
        return this.seleccionados.has(id);
    }

    /** Seleccionar o deseleccionar todos */
    toggleSeleccionarTodos(): void {
        if (this.seleccionados.size === this.items.length) {
            this.seleccionados.clear();
        } else {
            this.items.forEach(item => this.seleccionados.add(item.idItemUsuario));
        }
    }

    /** Elimina los items seleccionados */
    eliminarSeleccionados(): void {
        if (this.seleccionados.size === 0) return;

        const count = this.seleccionados.size;
        const msg = count === 1
            ? '¿Estás seguro de eliminar esta obra de tu biblioteca?'
            : `¿Estás seguro de eliminar ${count} obras de tu biblioteca?`;

        if (!confirm(msg)) return;

        const ids = Array.from(this.seleccionados);
        let completados = 0;
        let errores = 0;

        ids.forEach(id => {
            this.itemUsuarioService.delete(id).subscribe({
                next: () => {
                    completados++;
                    if (completados + errores === ids.length) {
                        this.postEliminacion(completados, errores);
                    }
                },
                error: () => {
                    errores++;
                    if (completados + errores === ids.length) {
                        this.postEliminacion(completados, errores);
                    }
                }
            });
        });
    }

    private postEliminacion(ok: number, err: number): void {
        this.seleccionados.clear();
        this.modoEdicion = false;

        // Recargar items
        const userId = this.authService.getUserId();
        const config = this.categoriasMap[this.currentSlug];
        if (userId && config) {
            this.cargarItems(userId, config.tipos);
        }
    }

    volverAlMenu(): void {
        this.router.navigate(['/biblioteca']);
    }

    /** Maneja la selección de un nuevo archivo de banner por el usuario */
    onBannerSelected(event: any): void {
        const file = event.target.files[0];
        if (!file) return;

        // Validar que sea imagen
        const validacion = this.archivoService.validateImageOnly(file);
        if (!validacion.valid) {
            alert('Por favor, selecciona un archivo de imagen válido.');
            return;
        }

        const userId = this.authService.getUserId();
        if (!userId) return;

        // Subir archivo
        this.archivoService.upload(file).subscribe({
            next: (res) => {
                const newRelativeUrl = res.url;
                this.bannerUrl = this.archivoService.getFullUrl(newRelativeUrl);

                // Obtener banners actuales
                const currentUser = this.authService.getStoredUser();
                let bannersObj: any = {};
                if (currentUser && currentUser.customBanners) {
                    try {
                        bannersObj = JSON.parse(currentUser.customBanners);
                    } catch (e) { }
                }

                // Actualizar la categoría actual
                bannersObj[this.currentSlug] = newRelativeUrl;
                const newBannersJson = JSON.stringify(bannersObj);

                // Guardar en backend
                this.usuarioService.updateBanners(userId, newBannersJson).subscribe({
                    next: () => {
                        // Actualizar en authService para reflejar el cambio en toda la sesión
                        this.authService.updateStoredBanners(newBannersJson);
                    },
                    error: (err) => console.error('Error guardando banner en backend', err)
                });
            },
            error: (err) => {
                console.error('Error subiendo banner', err);
                alert('Hubo un error al subir la imagen.');
            }
        });
    }
}
