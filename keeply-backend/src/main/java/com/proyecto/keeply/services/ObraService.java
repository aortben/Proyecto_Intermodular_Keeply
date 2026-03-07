package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import com.proyecto.keeply.repositories.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ObraService {

    private final ObraRepository obraRepository;

    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    public List<Obra> findByTipoObra(TipoObra tipoObra) {
        return obraRepository.findByTipoObra(tipoObra);
    }

    public List<Obra> findByTituloContaining(String titulo) {
        return obraRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public Obra save(Obra obra) {
        return obraRepository.save(obra);
    }

    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return obraRepository.existsById(id);
    }
}

