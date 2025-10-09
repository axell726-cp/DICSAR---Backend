package com.dicsar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{
    List<Notificacion> findByProducto(Producto producto);

}
