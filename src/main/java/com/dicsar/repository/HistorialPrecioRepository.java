package com.dicsar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.HistorialPrecio;
import com.dicsar.entity.Producto;

public interface HistorialPrecioRepository extends JpaRepository<HistorialPrecio, Long>{
    long countByProductoAndFechaCambioAfter(Producto producto, LocalDateTime fecha);
    List<HistorialPrecio> findByProductoIdProductoOrderByFechaCambioDesc(Long idProducto);
}
