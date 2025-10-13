package com.dicsar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dicsar.entity.HistorialPrecio;
import com.dicsar.repository.HistorialPrecioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialPrecioService {
	
	private final HistorialPrecioRepository historialPrecioRepository;

    public List<HistorialPrecio> obtenerHistorialPorProducto(Long idProducto) {
        return historialPrecioRepository.findByProductoIdProductoOrderByFechaCambioDesc(idProducto);
    }
}
