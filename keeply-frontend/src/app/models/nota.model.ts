export type TipoAdjunto = 'IMAGEN' | 'VIDEO' | 'AUDIO' | 'ENLACE';

export interface Adjunto {
    idAdjunto?: number;
    tipoAdjunto: TipoAdjunto;
    urlArchivo: string;
    fechaCreacion?: string;
}

export interface Nota {
    idNota?: number;
    textoNota: string;
    fechaCreacion?: string;
    adjuntos: Adjunto[];
}

/**
 * DTO para crear una nota (coincide con NotaRequestDTO del backend).
 */
export interface NotaRequest {
    idItemUsuario: number;
    textoNota: string;
    adjuntos: { tipoAdjunto: TipoAdjunto; urlArchivo: string }[];
}
