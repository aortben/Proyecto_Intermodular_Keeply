import { Injectable, signal, effect } from '@angular/core';

/**
 * Servicio encargado de gestionar el tema (claro/oscuro) de la aplicación.
 * Utiliza los signals de Angular para mantener el estado reactivo del tema de forma eficiente.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {

    // Signal que almacena si el tema actual es oscuro. Permite que los componentes reaccionen al cambio.
    isDarkMode = signal<boolean>(this.getInitialTheme());

    constructor() {
        // El effect se ejecuta al inicializar y cada vez que el signal 'isDarkMode' cambia de valor.
        // Asegura que el DOM y el localStorage siempre estén sincronizados con el estado.
        effect(() => {
            this.applyTheme(this.isDarkMode());
        });
    }

    /**
     * Alterna el valor del tema entre claro y oscuro.
     */
    toggleTheme(): void {
        this.isDarkMode.update(dark => !dark);
    }

    /**
     * Determina el tema inicial de la aplicación al cargar la página.
     * Prioriza la preferencia guardada por el usuario, y si no existe,
     * comprueba la configuración del sistema operativo.
     */
    private getInitialTheme(): boolean {
        const stored = localStorage.getItem('keeply_theme');
        if (stored !== null) {
            return stored === 'dark';
        }
        // Respetar preferencia nativa del sistema operativo o navegador
        return window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    /**
     * Aplica físicamente el tema a la interfaz modificando el atributo 'data-theme'
     * en el elemento <html>, lo que activa las variables CSS correspondientes.
     * También persiste la elección en el localStorage.
     * @param isDark true si se debe aplicar el tema oscuro.
     */
    private applyTheme(isDark: boolean): void {
        const theme = isDark ? 'dark' : 'light';
        // Inyecta el atributo en la raíz del documento para que afecte a toda la app
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('keeply_theme', theme);
    }
}
