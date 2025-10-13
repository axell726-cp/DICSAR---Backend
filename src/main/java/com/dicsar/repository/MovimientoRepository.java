package com.dicsar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.Movimiento;
import com.dicsar.enums.TipoMovimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long>{
	
	 List<Movimiento> findByProductoIdProductoOrderByFechaMovimientoDesc(Long idProducto);

	 List<Movimiento> findByTipoMovimiento(TipoMovimiento tipoMovimiento);
}
