package com.dicsar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dicsar.entity.UnidadMed;
import com.dicsar.repository.UnidadMedRepository;

@Service
public class UnidadMedService {

    private final UnidadMedRepository unidadMedRepository;

    public UnidadMedService(UnidadMedRepository unidadMedRepository) {
        this.unidadMedRepository = unidadMedRepository;
    }

    public List<UnidadMed> listar() {
        return unidadMedRepository.findAll();
    }

    public Optional<UnidadMed> obtener(Long id) {
        return unidadMedRepository.findById(id);
    }

    public UnidadMed guardar(UnidadMed unidadMed) {
        validarUnidadMed(unidadMed);
        return unidadMedRepository.save(unidadMed);
    }

    public void eliminar(Long id) {
        unidadMedRepository.deleteById(id);
    }

    private void validarUnidadMed(UnidadMed unidadMed) {
        // Validar nombre obligatorio
        if (!StringUtils.hasText(unidadMed.getNombre())) {
            throw new RuntimeException("El nombre de la unidad de medida es obligatorio.");
        }

        // Validar abreviatura obligatoria
        if (!StringUtils.hasText(unidadMed.getAbreviatura())) {
            throw new RuntimeException("La abreviatura de la unidad de medida es obligatoria.");
        }

        // Validar nombre único
        if (unidadMedRepository.existsByNombre(unidadMed.getNombre())) {
            throw new RuntimeException("Ya existe una unidad de medida con ese nombre.");
        }

        // Validar abreviatura única
        if (unidadMedRepository.existsByAbreviatura(unidadMed.getAbreviatura())) {
            throw new RuntimeException("Ya existe una unidad de medida con esa abreviatura.");
        }
    }
}
