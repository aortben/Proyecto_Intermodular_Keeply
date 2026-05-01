package com.proyecto.keeply.dto;

import com.proyecto.keeply.entities.TipoAdjunto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotaRequestDTO {
    private Integer idItemUsuario;
    private String textoNota;
    private List<AdjuntoDTO> adjuntos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdjuntoDTO {
        private TipoAdjunto tipoAdjunto;
        private String urlArchivo;
    }
}
