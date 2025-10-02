package com.dicsar.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dicsar.entity.Producto;
import com.dicsar.service.ProductoService;

@RestController
@RequestMapping("api/productos")
public class ProductoController {
	
	private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
    	return productoService.obtener(id)
                .map(p -> {
                    p.setNombre(producto.getNombre());
                    p.setDescripcion(producto.getDescripcion());
                    p.setPrecio(producto.getPrecio());
                    p.setStock(producto.getStock());
                    p.setFechaVencimiento(producto.getFechaVencimiento());
                    return ResponseEntity.ok(productoService.guardar(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam boolean nuevoEstado) {
        Optional<Producto> productoOpt = productoService.getOne(id);

        if (!productoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        Producto producto = productoOpt.get();

        // Regla 1: no desactivar si stock > 0
        if (!nuevoEstado && producto.getStock() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede inactivar un producto con stock disponible");
        }

        // Regla 2: no activar si ya está vencido
        if (nuevoEstado && producto.getFechaVencimiento() != null &&
                producto.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede activar un producto vencido");
        }

        producto.setEstado(nuevoEstado);
        producto.setFechaActualizacion(LocalDateTime.now());
        productoService.guardar(producto);

        return ResponseEntity.ok("Estado actualizado correctamente");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoService.getOne(id);

        if (!productoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        Producto producto = productoOpt.get();

        // Regla de negocio: no eliminar si el producto está activo
        if (Boolean.TRUE.equals(producto.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede eliminar un producto activo. Primero cámbielo a inactivo.");
        }

        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

}
