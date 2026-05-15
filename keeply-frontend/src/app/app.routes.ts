import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegistroComponent } from './components/registro/registro.component';
import { BusquedaComponent } from './components/busqueda/busqueda.component';
import { BibliotecaComponent } from './components/biblioteca/biblioteca.component';
import { CategoriaDetalleComponent } from './components/categoria-detalle/categoria-detalle.component';
import { DetalleItemComponent } from './components/detalle-item/detalle-item.component';
import { ComunidadComponent } from './components/comunidad/comunidad.component';
import { PerfilPublicoComponent } from './components/perfil-publico/perfil-publico.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'busqueda', component: BusquedaComponent },
    { path: 'biblioteca', component: BibliotecaComponent },
    { path: 'biblioteca/:categoria', component: CategoriaDetalleComponent },
    { path: 'item/:id', component: DetalleItemComponent },
    { path: 'comunidad', component: ComunidadComponent },
    { path: 'usuario/:nombreUsuario', component: PerfilPublicoComponent },
    { path: '**', redirectTo: '' }
];
