package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dicsar.entity.Proveedor;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRuc(String ruc);
    
    Optional<Proveedor> findByEmail(String email);

    boolean existsByRuc(String ruc);

    boolean existsByEmail(String email);
}
