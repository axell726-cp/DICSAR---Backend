package com.dicsar.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dicsar.entity.HistorialPrecio;
import com.dicsar.service.HistorialPrecioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/historial-precios")
@RequiredArgsConstructor
public class HistorialPrecioController {
	
	private final HistorialPrecioService historialPrecioService;

	@GetMapping("/producto/{idProducto}")
	public ResponseEntity<List<HistorialPrecio>> obtenerHistorialPorProducto(@PathVariable Long idProducto) {
		List<HistorialPrecio> historial = historialPrecioService.obtenerHistorialPorProducto(idProducto);
		return ResponseEntity.ok(historial);
	}
}
