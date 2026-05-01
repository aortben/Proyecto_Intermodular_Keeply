package com.proyecto.keeply.services;

import com.proyecto.keeply.dto.NotaRequestDTO;
import com.proyecto.keeply.entities.Adjunto;
import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Nota;
import com.proyecto.keeply.repositories.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotaService {

    private final NotaRepository notaRepository;
    private final ItemUsuarioService itemUsuarioService;

    public List<Nota> findByItemUsuarioId(Integer itemUsuarioId) {
        return itemUsuarioService.findById(itemUsuarioId)
                .map(notaRepository::findByItemUsuarioOrderByFechaCreacionDesc)
                .orElse(List.of());
    }

    public Optional<Nota> findById(Integer id) {
        return notaRepository.findById(id);
    }

    /**
     * Crea una Nota a partir del DTO, vinculando los adjuntos en cascada.
     */
    public Nota crearDesdeDTO(NotaRequestDTO dto) {
        ItemUsuario item = itemUsuarioService.findById(dto.getIdItemUsuario())
                .orElseThrow(() -> new RuntimeException(
                        "ItemUsuario no encontrado: " + dto.getIdItemUsuario()));

        Nota nota = Nota.builder()
                .itemUsuario(item)
                .textoNota(dto.getTextoNota())
                .build();

        // Añadir adjuntos si existen
        if (dto.getAdjuntos() != null) {
            for (NotaRequestDTO.AdjuntoDTO adj : dto.getAdjuntos()) {
                Adjunto adjunto = Adjunto.builder()
                        .nota(nota)
                        .tipoAdjunto(adj.getTipoAdjunto())
                        .urlArchivo(adj.getUrlArchivo())
                        .build();
                nota.getAdjuntos().add(adjunto);
            }
        }

        return notaRepository.save(nota);
    }

    public void deleteById(Integer id) {
        notaRepository.deleteById(id);
    }
}
