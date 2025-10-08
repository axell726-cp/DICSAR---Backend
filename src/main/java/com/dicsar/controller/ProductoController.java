package com.dicsar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.entity.Producto;
import com.dicsar.service.ProductoService;

import jakarta.validation.Valid;

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
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO dto) {
        Producto producto = productoService.guardarDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        Producto producto = productoService.actualizar(id, dto);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long id, @RequestParam boolean nuevoEstado) {
        productoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok("Estado actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        productoService.eliminarConRegla(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}