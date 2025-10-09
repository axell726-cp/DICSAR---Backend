package com.dicsar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoAlerta;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{
	boolean existsByProductoIdProductoAndTipo(Long productoId, TipoAlerta tipo);
    List<Notificacion> findByProducto(Producto producto);

}
