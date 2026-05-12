import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, SlicePipe } from '@angular/common';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { BusquedaService } from '../../services/busqueda.service';
import { ObraService } from '../../services/obra.service';
import { ItemUsuarioService } from '../../services/item-usuario.service';
import { AuthService } from '../../services/auth.service';
import { ResultadoBusqueda } from '../../models/resultado-busqueda.model';
import { TipoObra } from '../../models/obra.model';

@Component({
    selector: 'app-busqueda',
    standalone: true,
    imports: [FormsModule, NgFor, NgIf, SlicePipe, NavbarComponent, TranslateModule],
    templateUrl: './busqueda.component.html',
    styleUrl: './busqueda.component.css'
})
export class BusquedaComponent implements OnInit {
    private busquedaService = inject(BusquedaService);
    private obraService = inject(ObraService);
    private itemUsuarioService = inject(ItemUsuarioService);
    private authService = inject(AuthService);
    private router = inject(Router);

    query = '';
    tipoSeleccionado: TipoObra = 'PELICULA';
    resultados: ResultadoBusqueda[] = [];
    cargando = false;
    mensaje = '';

    // Set de idExternoApi que el usuario ya tiene en su biblioteca
    obrasEnBiblioteca = new Set<string>();
    // Set de ids que se están añadiendo (loading state)
    agregando = new Set<string>();

    tipos: { valor: TipoObra; label: string }[] = [
        { valor: 'PELICULA', label: 'Películas' },
        { valor: 'SERIE', label: 'Series' },
        { valor: 'ANIME', label: 'Anime' },
        { valor: 'MANGA', label: 'Manga' },
        { valor: 'VIDEOJUEGO', label: 'Videojuegos' },
        { valor: 'LIBRO', label: 'Libros' },
        { valor: 'COMIC', label: 'Cómics' }
    ];

    ngOnInit(): void {
        this.cargarObrasDelUsuario();
    }

    /** Carga las obras que el usuario ya tiene para evitar duplicados */
    private cargarObrasDelUsuario(): void {
        const userId = this.authService.getUserId();
        if (!userId) return;

        this.itemUsuarioService.getByUsuarioId(userId).subscribe({
            next: (items) => {
                this.obrasEnBiblioteca.clear();
                items.forEach(item => {
                    if (item.obra.idExternoApi) {
                        this.obrasEnBiblioteca.add(item.obra.idExternoApi);
                    }
                });
            }
        });
    }

    buscar(): void {
        if (!this.query.trim()) return;
        this.cargando = true;
        this.mensaje = '';
        this.resultados = [];

        this.busquedaService.buscarUnificada(this.query, this.tipoSeleccionado).subscribe({
            next: (res) => {
                this.resultados = res;
                this.cargando = false;
                if (res.length === 0) {
                    this.mensaje = 'No se encontraron resultados.';
                }
            },
            error: (err) => {
                this.cargando = false;
                this.mensaje = 'Error al buscar. Inténtalo de nuevo.';
                console.error(err);
            }
        });
    }

    /** Comprueba si una obra ya está en la biblioteca */
    estaEnBiblioteca(resultado: ResultadoBusqueda): boolean {
        return this.obrasEnBiblioteca.has(resultado.idExterno);
    }

    /** Comprueba si una obra se está añadiendo */
    estaAgregando(resultado: ResultadoBusqueda): boolean {
        return this.agregando.has(resultado.idExterno);
    }

    agregarABiblioteca(resultado: ResultadoBusqueda): void {
        const usuarioId = this.authService.getUserId();
        if (!usuarioId) {
            this.mensaje = 'Debes iniciar sesión para añadir obras a tu biblioteca.';
            return;
        }

        // Evitar duplicados
        if (this.estaEnBiblioteca(resultado)) return;

        this.agregando.add(resultado.idExterno);

        // Sanitizar fecha: solo enviarla si tiene formato ISO válido (yyyy-MM-dd)
        let fecha: string | undefined = undefined;
        if (resultado.fechaLanzamiento) {
            const dateStr = resultado.fechaLanzamiento.toString();
            // Acepta "2024-01-15" o similar
            if (/^\d{4}-\d{2}-\d{2}/.test(dateStr)) {
                fecha = dateStr.substring(0, 10); // Solo yyyy-MM-dd
            }
        }

        const obra: any = {
            titulo: resultado.titulo,
            descripcion: resultado.descripcion || '',
            tipoObra: resultado.tipoObra,
            autorCreador: resultado.autorCreador || '',
            urlImagenPrincipal: resultado.imagenUrl || '',
            idExternoApi: resultado.idExterno,
            detallesJson: resultado.detallesJson || null,
            origenDatos: 'API'
        };

        // Solo incluir fecha si es válida
        if (fecha) {
            obra.fechaLanzamiento = fecha;
        }

        this.obraService.create(obra).subscribe({
            next: (obraGuardada) => {
                const item = {
                    usuario: { idUsuario: usuarioId } as any,
                    obra: { idObra: obraGuardada.idObra } as any,
                    estado: 'Pendiente' as const
                };

                this.itemUsuarioService.create(item).subscribe({
                    next: () => {
                        this.obrasEnBiblioteca.add(resultado.idExterno);
                        this.agregando.delete(resultado.idExterno);
                        this.mensaje = `"${resultado.titulo}" añadido a tu biblioteca.`;
                    },
                    error: (err) => {
                        this.agregando.delete(resultado.idExterno);
                        this.mensaje = 'Error al vincular la obra al usuario.';
                        console.error(err);
                    }
                });
            },
            error: (err) => {
                this.agregando.delete(resultado.idExterno);
                this.mensaje = 'Error al guardar la obra.';
                console.error(err);
            }
        });
    }

    volverABiblioteca(): void {
        this.router.navigate(['/biblioteca']);
    }
}
