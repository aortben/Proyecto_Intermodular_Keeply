import { Usuario } from './usuario.model';
import { Obra } from './obra.model';

export type EstadoItem = 'Pendiente' | 'En_Progreso' | 'Completado' | 'Abandonado';

export interface ItemUsuario {
    idItemUsuario?: number;
    usuario: Usuario;
    obra: Obra;
    estado: EstadoItem;
    valoracionPersonal?: number;
    fechaAdicion?: string;
}
