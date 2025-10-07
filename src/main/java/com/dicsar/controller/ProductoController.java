package com.dicsar.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    p.setCodigo(producto.getCodigo());
                    p.setPrecio(producto.getPrecio());
                    p.setStockActual(producto.getStockActual());
                    p.setStockMinimo(producto.getStockMinimo());
                    p.setFechaVencimiento(producto.getFechaVencimiento());
                    p.setCategoria(producto.getCategoria());
                    p.setProveedor(producto.getProveedor());
                    p.setUnidadMedida(producto.getUnidadMedida());
                    p.setFechaActualizacion(LocalDateTime.now());
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

        if (!nuevoEstado && producto.getStockActual() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede inactivar un producto con stock disponible");
        }

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

        if (Boolean.TRUE.equals(producto.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede eliminar un producto activo. Primero cámbielo a inactivo.");
        }

        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }


    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return productoService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}