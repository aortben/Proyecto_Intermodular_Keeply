export type TipoObra = 'VIDEOJUEGO' | 'PELICULA' | 'SERIE' | 'ANIME' | 'MANGA' | 'LIBRO' | 'COMIC';
export type OrigenDatos = 'MANUAL' | 'API';

export interface Obra {
    idObra?: number;
    titulo: string;
    descripcion?: string;
    tipoObra: TipoObra;
    autorCreador?: string;
    fechaLanzamiento?: string;
    urlImagenPrincipal?: string;
    idExternoApi?: string;
    detallesJson?: string;
    origenDatos?: OrigenDatos;
}
