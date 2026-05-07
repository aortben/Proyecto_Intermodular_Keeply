import { ItemUsuario } from './item-usuario.model';

export type TipoContenido = 'Nota' | 'Imagen' | 'Video' | 'Audio';

export interface ContenidoUsuario {
    idContenido?: number;
    itemUsuario: ItemUsuario;
    tipoContenido: TipoContenido;
    textoNota?: string;
    urlArchivo?: string;
    fechaCreacion?: string;
}
