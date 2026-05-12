import { Component, inject, HostListener, AfterViewInit, NgZone, ViewChild, ElementRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { environment } from '../../../environments/environment';

declare const google: any;

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [RouterLink, AsyncPipe, NgIf, FormsModule, TranslateModule],
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements AfterViewInit {
    private authService = inject(AuthService);
    private router = inject(Router);
    private ngZone = inject(NgZone);
    translate = inject(TranslateService);
    themeService = inject(ThemeService);

    @ViewChild('googleBtnLogin', { static: false }) googleBtnLogin!: ElementRef;
    @ViewChild('googleBtnRegistro', { static: false }) googleBtnRegistro!: ElementRef;

    currentUser$ = this.authService.currentUser$;

    menuAbierto: string | null = null;
    menuMobilAbierto = false;
    modoAuth: 'login' | 'registro' = 'login';

    loginNombreUsuario = '';
    loginPassword = '';
    regNombreUsuario = '';
    regEmail = '';
    regPassword = '';
    authError = '';
    authExito = '';

    currentLang = 'es';
    private googleInitialized = false;

    constructor() {
        const saved = localStorage.getItem('keeply_lang') || 'es';
        this.currentLang = saved;
        this.translate.use(saved);
    }

    ngAfterViewInit(): void {
        this.initGoogleAuth();
    }

    switchLang(lang: string): void {
        this.currentLang = lang;
        this.translate.use(lang);
        localStorage.setItem('keeply_lang', lang);
    }

    private initGoogleAuth(): void {
        const interval = setInterval(() => {
            if (typeof google !== 'undefined' && google.accounts) {
                clearInterval(interval);
                google.accounts.id.initialize({
                    client_id: environment.googleClientId,
                    callback: (response: any) => this.handleGoogleCallback(response)
                });
                this.googleInitialized = true;
                this.renderGoogleButtons();
            }
        }, 200);
        setTimeout(() => clearInterval(interval), 10000);
    }

    private renderGoogleButtons(): void {
        setTimeout(() => {
            if (this.googleBtnLogin?.nativeElement) {
                google.accounts.id.renderButton(this.googleBtnLogin.nativeElement, {
                    type: 'standard', theme: 'filled_black', size: 'large',
                    text: 'signin_with', shape: 'rectangular', width: 240
                });
            }
            if (this.googleBtnRegistro?.nativeElement) {
                google.accounts.id.renderButton(this.googleBtnRegistro.nativeElement, {
                    type: 'standard', theme: 'filled_black', size: 'large',
                    text: 'signup_with', shape: 'rectangular', width: 240
                });
            }
        }, 100);
    }

    private handleGoogleCallback(response: any): void {
        this.ngZone.run(() => {
            this.authError = '';
            this.authService.loginWithGoogle(response.credential).subscribe({
                next: () => {
                    this.menuAbierto = null;
                    this.limpiarCampos();
                    this.router.navigate(['/biblioteca']);
                },
                error: (err) => {
                    this.authError = err.error?.error || this.translate.instant('ERRORS.GOOGLE_ERROR');
                }
            });
        });
    }

    toggleMenu(menu: string, event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.menuAbierto = this.menuAbierto === menu ? null : menu;
        this.authError = '';
        this.authExito = '';
        if (menu === 'cuenta' && this.menuAbierto === 'cuenta' && this.googleInitialized) {
            this.renderGoogleButtons();
        }
    }

    @HostListener('document:click')
    cerrarMenus(): void {
        this.menuAbierto = null;
    }

    onPopoverClick(event: Event): void {
        event.stopPropagation();
    }

    toggleModoAuth(event?: Event): void {
        if (event) event.preventDefault();
        this.modoAuth = this.modoAuth === 'login' ? 'registro' : 'login';
        this.authError = '';
        this.authExito = '';
        if (this.googleInitialized) {
            this.renderGoogleButtons();
        }
    }

    toggleMenuMobil(): void {
        this.menuMobilAbierto = !this.menuMobilAbierto;
    }

    cerrarMenuMobil(): void {
        this.menuMobilAbierto = false;
    }

    toggleTheme(): void {
        this.themeService.toggleTheme();
    }

    loginInline(): void {
        if (!this.loginNombreUsuario || !this.loginPassword) {
            this.authError = this.translate.instant('ERRORS.FILL_ALL_FIELDS');
            return;
        }
        this.authService.login({ nombreUsuario: this.loginNombreUsuario, contrasena: this.loginPassword }).subscribe({
            next: () => {
                this.menuAbierto = null;
                this.limpiarCampos();
            },
            error: () => this.authError = this.translate.instant('ERRORS.INVALID_CREDENTIALS')
        });
    }

    registroInline(): void {
        if (!this.regNombreUsuario || !this.regPassword || !this.regEmail) {
            this.authError = this.translate.instant('ERRORS.FILL_ALL_FIELDS');
            return;
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.regEmail)) {
            this.authError = this.translate.instant('ERRORS.INVALID_EMAIL');
            return;
        }
        this.authService.register({
            nombreUsuario: this.regNombreUsuario,
            contrasena: this.regPassword,
            email: this.regEmail
        }).subscribe({
            next: () => {
                this.menuAbierto = null;
                this.limpiarCampos();
            },
            error: () => this.authError = this.translate.instant('ERRORS.REGISTER_FAILED')
        });
    }

    logout(): void {
        this.authService.logout();
        this.menuAbierto = null;
        this.menuMobilAbierto = false;
        this.router.navigate(['/']);
    }

    irABiblioteca(): void {
        this.menuMobilAbierto = false;
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/biblioteca']);
        } else {
            this.menuAbierto = 'cuenta';
            this.modoAuth = 'login';
            this.authError = '';
            this.authExito = '';
        }
    }

    private limpiarCampos(): void {
        this.loginNombreUsuario = '';
        this.loginPassword = '';
        this.regNombreUsuario = '';
        this.regPassword = '';
        this.regEmail = '';
        this.authError = '';
        this.authExito = '';
        this.modoAuth = 'login';
    }
}