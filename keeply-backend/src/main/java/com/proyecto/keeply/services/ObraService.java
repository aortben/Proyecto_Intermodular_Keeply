package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import com.proyecto.keeply.repositories.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones CRUD sobre el catálogo maestro de "Obras".
 * Una Obra representa el elemento físico o digital base (un libro, una película, un juego)
 * antes de que un usuario lo añada a su biblioteca personal.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ObraService {

    private final ObraRepository obraRepository;

    /**
     * Recupera todas las obras guardadas en la base de datos central.
     * @return Lista de obras.
     */
    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    /**
     * Busca una obra específica en el catálogo mediante su ID interno.
     * @param id ID de la obra.
     * @return Optional con la obra si se encuentra.
     */
    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    /**
     * Filtra las obras del catálogo por su tipo (Película, Libro, Anime...).
     * @param tipoObra Enumerado con el tipo.
     * @return Lista de obras de esa categoría.
     */
    public List<Obra> findByTipoObra(TipoObra tipoObra) {
        return obraRepository.findByTipoObra(tipoObra);
    }

    /**
     * Realiza una búsqueda parcial en el catálogo por el título de la obra,
     * ignorando mayúsculas y minúsculas.
     * @param titulo Fragmento del título a buscar.
     * @return Lista de obras que coinciden.
     */
    public List<Obra> findByTituloContaining(String titulo) {
        return obraRepository.findByTituloContainingIgnoreCase(titulo);
    }

    /**
     * Guarda una nueva obra en el catálogo global o actualiza una existente.
     * Normalmente se llama a este método tras traer un resultado de la API externa
     * y antes de asociarlo al usuario.
     * @param obra La obra a guardar.
     * @return La obra ya persistida.
     */
    public Obra save(Obra obra) {
        return obraRepository.save(obra);
    }

    /**
     * Elimina una obra del catálogo maestro.
     * @param id ID de la obra a borrar.
     */
    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    /**
     * Comprueba de manera rápida si una obra existe en el catálogo.
     */
    public boolean existsById(Integer id) {
        return obraRepository.existsById(id);
    }
}
