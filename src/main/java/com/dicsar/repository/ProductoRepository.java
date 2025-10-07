package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dicsar.entity.Producto;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar producto por código
    Optional<Producto> findByCodigo(String codigo);
    
    // Validar existencia de producto por código
    boolean existsByCodigo(String codigo);
}
