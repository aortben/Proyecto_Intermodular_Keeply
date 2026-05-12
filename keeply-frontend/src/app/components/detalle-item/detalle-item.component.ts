import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgClass, SlicePipe } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { ItemUsuarioService } from '../../services/item-usuario.service';
import { NotaService } from '../../services/nota.service';
import { ArchivoService } from '../../services/archivo.service';
import { ItemUsuario, EstadoItem } from '../../models/item-usuario.model';
import { Nota, NotaRequest, TipoAdjunto } from '../../models/nota.model';

@Component({
    selector: 'app-detalle-item',
    standalone: true,
    imports: [FormsModule, NgFor, NgIf, NgClass, SlicePipe, NavbarComponent, TranslateModule],
    templateUrl: './detalle-item.component.html',
    styleUrls: ['./detalle-item.component.scss']
})
export class DetalleItemComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private itemService = inject(ItemUsuarioService);
    private notaService = inject(NotaService);
    private archivoService = inject(ArchivoService);

    item: ItemUsuario | null = null;
    notas: Nota[] = [];
    cargando = true;

    // Edicion de propiedades del item
    editandoPropiedades = false;
    estadoEditado: EstadoItem = 'Pendiente';
    valoracionEditada: number | null = null;
    guardandoPropiedades = false;
    mensajeExito = '';

    estados: { valor: EstadoItem; label: string }[] = [
        { valor: 'Pendiente', label: 'Pendiente' },
        { valor: 'En_Progreso', label: 'En progreso' },
        { valor: 'Completado', label: 'Completado' },
        { valor: 'Abandonado', label: 'Abandonado' }
    ];

    // Estado del editor de notas
    editorAbierto = false;
    nuevoTexto = '';
    adjuntosPendientes: { tipoAdjunto: TipoAdjunto; urlArchivo: string }[] = [];
    nuevaUrlAdjunto = '';
    nuevoTipoAdjunto: TipoAdjunto = 'IMAGEN';
    subiendoArchivo = false;

    // Edicion de nota existente
    editandoNotaId: number | null = null;
    textoEditandoNota = '';

    tiposAdjunto: TipoAdjunto[] = ['IMAGEN', 'VIDEO', 'AUDIO'];

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.cargarItem(id);
            this.cargarNotas(id);
        }
    }

    cargarItem(id: number): void {
        this.itemService.getById(id).subscribe({
            next: (res) => {
                this.item = res;
                this.estadoEditado = res.estado;
                this.valoracionEditada = res.valoracionPersonal ?? null;
                this.cargando = false;
            },
            error: () => this.cargando = false
        });
    }

    cargarNotas(itemId: number): void {
        this.notaService.getByItemId(itemId).subscribe({
            next: (res) => this.notas = res,
            error: (err) => console.error('Error al cargar notas:', err)
        });
    }

    // ========== Edicion de estado y valoracion ==========

    toggleEditarPropiedades(): void {
        this.editandoPropiedades = !this.editandoPropiedades;
        this.mensajeExito = '';
        if (this.editandoPropiedades && this.item) {
            this.estadoEditado = this.item.estado;
            this.valoracionEditada = this.item.valoracionPersonal ?? null;
        }
    }

    guardarPropiedades(): void {
        if (!this.item || !this.item.idItemUsuario) return;

        this.guardandoPropiedades = true;

        const updatedItem: ItemUsuario = {
            ...this.item,
            estado: this.estadoEditado,
            valoracionPersonal: this.valoracionEditada ?? undefined
        };

        this.itemService.update(this.item.idItemUsuario, updatedItem).subscribe({
            next: (res) => {
                this.item = res;
                this.editandoPropiedades = false;
                this.guardandoPropiedades = false;
                this.mensajeExito = 'Cambios guardados correctamente';
                setTimeout(() => this.mensajeExito = '', 3000);
            },
            error: (err) => {
                console.error('Error al guardar cambios:', err);
                this.guardandoPropiedades = false;
            }
        });
    }

    // ========== Editor de notas ==========

    toggleEditor(): void {
        this.editorAbierto = !this.editorAbierto;
        if (!this.editorAbierto) {
            this.limpiarEditor();
        }
    }

    agregarAdjuntoUrl(): void {
        if (!this.nuevaUrlAdjunto.trim()) return;
        this.adjuntosPendientes.push({
            tipoAdjunto: this.nuevoTipoAdjunto,
            urlArchivo: this.nuevaUrlAdjunto.trim()
        });
        this.nuevaUrlAdjunto = '';
    }

    /** Maneja la seleccion de archivo desde el explorador */
    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (!input.files || input.files.length === 0) return;

        const file = input.files[0];
        this.subiendoArchivo = true;

        // Detectar tipo de adjunto segun el archivo
        let tipo: TipoAdjunto = 'IMAGEN';
        if (file.type.startsWith('video/')) {
            tipo = 'VIDEO';
        } else if (file.type.startsWith('audio/')) {
            tipo = 'AUDIO';
        }

        this.archivoService.upload(file).subscribe({
            next: (res) => {
                const fullUrl = this.archivoService.getFullUrl(res.url);
                this.adjuntosPendientes.push({
                    tipoAdjunto: tipo,
                    urlArchivo: fullUrl
                });
                this.subiendoArchivo = false;
                // Limpiar el input para permitir subir el mismo archivo de nuevo
                input.value = '';
            },
            error: (err) => {
                console.error('Error al subir archivo:', err);
                this.subiendoArchivo = false;
                input.value = '';
            }
        });
    }

    eliminarAdjuntoPendiente(index: number): void {
        this.adjuntosPendientes.splice(index, 1);
    }

    guardarNota(): void {
        if (!this.item) return;
        // Permitir guardar si hay texto O adjuntos (o ambos)
        if (!this.nuevoTexto.trim() && this.adjuntosPendientes.length === 0) return;

        const request: NotaRequest = {
            idItemUsuario: this.item.idItemUsuario!,
            textoNota: this.nuevoTexto.trim(),
            adjuntos: this.adjuntosPendientes
        };

        this.notaService.create(request).subscribe({
            next: () => {
                this.cargarNotas(this.item!.idItemUsuario!);
                this.limpiarEditor();
                this.editorAbierto = false;
            },
            error: (err) => console.error('Error al crear nota:', err)
        });
    }

    // ========== Edicion de nota existente ==========

    iniciarEdicionNota(nota: Nota): void {
        this.editandoNotaId = nota.idNota!;
        this.textoEditandoNota = nota.textoNota || '';
    }

    cancelarEdicionNota(): void {
        this.editandoNotaId = null;
        this.textoEditandoNota = '';
    }

    guardarEdicionNota(nota: Nota): void {
        if (!this.item) return;

        // Eliminar la nota vieja y crear una nueva con el texto editado
        // (ya que no hay endpoint PUT para notas, recreamos)
        const request: NotaRequest = {
            idItemUsuario: this.item.idItemUsuario!,
            textoNota: this.textoEditandoNota.trim(),
            adjuntos: nota.adjuntos ? nota.adjuntos.map(a => ({
                tipoAdjunto: a.tipoAdjunto,
                urlArchivo: a.urlArchivo
            })) : []
        };

        this.notaService.delete(nota.idNota!).subscribe({
            next: () => {
                this.notaService.create(request).subscribe({
                    next: () => {
                        this.editandoNotaId = null;
                        this.textoEditandoNota = '';
                        this.cargarNotas(this.item!.idItemUsuario!);
                    },
                    error: (err) => console.error('Error al recrear nota:', err)
                });
            },
            error: (err) => console.error('Error al eliminar nota original:', err)
        });
    }

    eliminarNota(id: number): void {
        if (!confirm('Eliminar esta nota?')) return;
        this.notaService.delete(id).subscribe({
            next: () => this.cargarNotas(this.item!.idItemUsuario!),
            error: (err) => console.error('Error al eliminar nota:', err)
        });
    }

    eliminarItem(): void {
        if (!this.item || !this.item.idItemUsuario) return;

        if (confirm('Estas seguro de que quieres eliminar esta obra de tu biblioteca?')) {
            this.itemService.delete(this.item.idItemUsuario).subscribe({
                next: () => {
                    this.router.navigate(['/biblioteca']);
                },
                error: (err) => console.error('Error al eliminar el item:', err)
            });
        }
    }

    volver(): void {
        this.router.navigate(['/biblioteca']);
    }

    getEstadoLabel(estado: string): string {
        const found = this.estados.find(e => e.valor === estado);
        return found ? found.label : estado;
    }

    private limpiarEditor(): void {
        this.nuevoTexto = '';
        this.adjuntosPendientes = [];
        this.nuevaUrlAdjunto = '';
    }
}
