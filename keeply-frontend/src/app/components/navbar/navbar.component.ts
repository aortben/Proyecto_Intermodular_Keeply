import { Component, inject, HostListener, AfterViewInit, OnInit, NgZone, ViewChild, ElementRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';
import { ArchivoService } from '../../services/archivo.service';
import { UsuarioService } from '../../services/usuario.service';
import { SeguimientoService } from '../../services/seguimiento.service';
import { ThemeService } from '../../services/theme.service';
import { environment } from '../../../environments/environment';

declare const google: any;

interface AvatarOption {
    id: string;
    url: string;
}

/**
 * Componente principal de navegación (Navbar).
 * Controla el menú desplegable, el panel de usuario, el cambio de tema, idioma y la visualización de avatares.
 */
@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [RouterLink, AsyncPipe, NgIf, NgFor, FormsModule, TranslateModule],
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements AfterViewInit, OnInit {
    private authService = inject(AuthService);
    private archivoService = inject(ArchivoService);
    private usuarioService = inject(UsuarioService);
    private seguimientoService = inject(SeguimientoService);
    private router = inject(Router);
    private ngZone = inject(NgZone);
    translate = inject(TranslateService);
    themeService = inject(ThemeService);

    // Observable que emite el estado del usuario logueado en tiempo real
    currentUser$ = this.authService.currentUser$;

    // Controles de la interfaz de usuario (UI)
    menuAbierto: string | null = null;
    menuMobilAbierto = false;
    editandoAvatar = false;
    subiendoAvatar = false;

    // Contadores sociales
    numSeguidores = 0;
    numSiguiendo = 0;

    currentLang = 'es';

    // Lista de avatares por defecto para la edición del perfil
    avatarOptions: AvatarOption[] = [
        { id: 'av1', url: 'assets/images/avatar1.jpg' },
        { id: 'av2', url: 'assets/images/avatar2.jpg' },
        { id: 'av3', url: 'assets/images/avatar3.jpg' },
        { id: 'av4', url: 'assets/images/avatar4.jpg' },
        { id: 'av5', url: 'assets/images/avatar5.jpg' }
    ];

    /**
     * Constructor que inicializa el idioma base de la aplicación desde el localStorage.
     */
    constructor() {
        const saved = localStorage.getItem('keeply_lang') || 'es';
        this.currentLang = saved;
        this.translate.use(saved);
    }

    ngOnInit(): void {
        this.currentUser$.subscribe(user => {
            if (user && user.idUsuario) {
                this.cargarEstadisticasSociales(user.idUsuario);
            }
        });
    }

    cargarEstadisticasSociales(id: number) {
        this.seguimientoService.getSeguidores(id).subscribe(s => this.numSeguidores = s.length);
        this.seguimientoService.getSeguidos(id).subscribe(s => this.numSiguiendo = s.length);
    }

    ngAfterViewInit(): void { }

    /**
     * Cambia el idioma actual de la aplicación.
     * @param lang Código del idioma (ej: 'es' o 'en').
     */
    switchLang(lang: string): void {
        this.currentLang = lang;
        this.translate.use(lang);
        localStorage.setItem('keeply_lang', lang);
    }

    /**
     * Alterna la visibilidad de los menús desplegables (Info, Redes, Cuenta).
     */
    toggleMenu(menu: string, event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.menuAbierto = this.menuAbierto === menu ? null : menu;
        this.editandoAvatar = false;
    }

    /**
     * Cierra cualquier menú desplegable si se hace clic fuera de él.
     */
    @HostListener('document:click')
    cerrarMenus(): void {
        this.menuAbierto = null;
        this.editandoAvatar = false;
    }

    /**
     * Evita que el menú se cierre al hacer clic dentro de su contenido.
     */
    onPopoverClick(event: Event): void {
        event.stopPropagation();
    }

    /**
     * Alterna el menú lateral en dispositivos móviles.
     */
    toggleMenuMobil(): void {
        this.menuMobilAbierto = !this.menuMobilAbierto;
    }

    cerrarMenuMobil(): void {
        this.menuMobilAbierto = false;
    }

    toggleTheme(): void {
        this.themeService.toggleTheme();
    }

    toggleEditarAvatar(): void {
        this.editandoAvatar = !this.editandoAvatar;
    }

    /**
     * Cambia el avatar del usuario por uno de los predefinidos.
     * Almacena una cadena especial ('preset:id:color:inicial') en la base de datos.
     */
    selectPreloadedAvatar(av: AvatarOption): void {
        const user = this.authService.getStoredUser();
        if (!user) return;
        
        const avatarUrl = av.url;
        this.usuarioService.updateAvatar(user.idUsuario, avatarUrl).subscribe({
            next: () => {
                this.authService.updateStoredAvatar(avatarUrl);
                this.editandoAvatar = false;
            }
        });
    }

    /**
     * Maneja la subida de un nuevo archivo como imagen de perfil desde el panel.
     */
    onAvatarUpload(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (!input.files || input.files.length === 0) return;

        const file = input.files[0];
        const validation = this.archivoService.validateImageOnly(file);
        if (!validation.valid) {
            alert(this.translate.instant(validation.error!));
            input.value = '';
            return;
        }

        this.subiendoAvatar = true;
        this.archivoService.uploadAvatar(file).subscribe({
            next: (res) => {
                const fullUrl = this.archivoService.getFullUrl(res.url);
                const user = this.authService.getStoredUser();
                if (user) {
                    this.usuarioService.updateAvatar(user.idUsuario, fullUrl).subscribe({
                        next: () => {
                            this.authService.updateStoredAvatar(fullUrl);
                            this.subiendoAvatar = false;
                            this.editandoAvatar = false;
                        }
                    });
                }
                input.value = '';
            },
            error: () => {
                this.subiendoAvatar = false;
                input.value = '';
            }
        });
    }

    /**
     * Devuelve la url de avatar a mostrar o la de por defecto.
     * Incluye compatibilidad con los antiguos avatares predefinidos.
     */
    getUserAvatarUrl(user: any): string {
        if (!user || !user.avatarUrl) {
            return 'assets/images/default-avatar.png';
        }
        
        // Compatibilidad hacia atrás: si la BD tiene guardado "preset:avX:..."
        if (user.avatarUrl.startsWith('preset:av')) {
            const presetId = user.avatarUrl.split(':')[1]; // 'av1', 'av2', etc.
            return `assets/images/avatar${presetId.replace('av', '')}.jpg`;
        }
        
        return user.avatarUrl;
    }

    /**
     * Cierra sesión y redirige a la pantalla de inicio.
     */
    logout(): void {
        this.authService.logout();
        this.menuAbierto = null;
        this.menuMobilAbierto = false;
        this.router.navigate(['/']);
    }

    /**
     * Lógica de navegación del botón de biblioteca.
     * Exige que el usuario esté logueado.
     */
    irABiblioteca(): void {
        this.menuMobilAbierto = false;
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/biblioteca']);
        } else {
            this.router.navigate(['/login']);
        }
    }
}