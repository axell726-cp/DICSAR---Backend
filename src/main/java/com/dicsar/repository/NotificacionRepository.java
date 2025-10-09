package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.Notificacion;
import com.dicsar.enums.TipoAlerta;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{
	boolean existsByProductoIdProductoAndTipo(Long productoId, TipoAlerta tipo);
}
