package com.proyecto.keeply.dto;

import com.proyecto.keeply.entities.TipoObra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoBusquedaDTO {
    private String idExterno;
    private String titulo;
    private String descripcion;
    private String imagenUrl;
    private TipoObra tipoObra;
    private String autorCreador;
    private LocalDate fechaLanzamiento;
    private String origenApi;
    private String detallesJson;
}


