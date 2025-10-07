package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dicsar.entity.ReporteVenta;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteVentaRepository extends JpaRepository<ReporteVenta, Long> {

    // Buscar ventas por rango de fechas
    @Query("SELECT r FROM ReporteVenta r WHERE r.fechaVenta BETWEEN :inicio AND :fin")
    List<ReporteVenta> findByFechaVentaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Productos más vendidos
    @Query("SELECT r.producto.nombre, SUM(r.cantidad) FROM ReporteVenta r GROUP BY r.producto.nombre ORDER BY SUM(r.cantidad) DESC")
    List<Object[]> obtenerProductosMasVendidos();

    // Total de ventas por cliente
    @Query("SELECT r.cliente.nombre, SUM(r.total) FROM ReporteVenta r GROUP BY r.cliente.nombre")
    List<Object[]> obtenerVentasPorCliente();

    // Totales mensuales
    @Query("SELECT MONTH(r.fechaVenta), SUM(r.total) FROM ReporteVenta r GROUP BY MONTH(r.fechaVenta)")
    List<Object[]> obtenerTotalesMensuales();
}
