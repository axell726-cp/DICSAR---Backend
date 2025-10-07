package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dicsar.entity.UnidadMed;
import java.util.Optional;

public interface UnidadMedRepository extends JpaRepository<UnidadMed, Long> {
    
    // Buscar por nombre o abreviatura
    Optional<UnidadMed> findByNombre(String nombre);
    boolean existsByNombre(String nombre);

    Optional<UnidadMed> findByAbreviatura(String abreviatura);
    boolean existsByAbreviatura(String abreviatura);
}
