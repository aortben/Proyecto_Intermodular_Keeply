import { TipoObra } from './obra.model';

export interface ResultadoBusqueda {
    idExterno: string;
    titulo: string;
    descripcion?: string;
    imagenUrl?: string;
    tipoObra: TipoObra;
    autorCreador?: string;
    fechaLanzamiento?: string;
    origenApi?: string;
    detallesJson?: string;
}
