import { Injectable, signal, effect } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {

    isDarkMode = signal<boolean>(this.getInitialTheme());

    constructor() {
        // Aplicar el tema al arrancar
        effect(() => {
            this.applyTheme(this.isDarkMode());
        });
    }

    toggleTheme(): void {
        this.isDarkMode.update(dark => !dark);
    }

    private getInitialTheme(): boolean {
        const stored = localStorage.getItem('keeply_theme');
        if (stored !== null) {
            return stored === 'dark';
        }
        // Respetar preferencia del sistema
        return window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    private applyTheme(isDark: boolean): void {
        const theme = isDark ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('keeply_theme', theme);
    }
}
