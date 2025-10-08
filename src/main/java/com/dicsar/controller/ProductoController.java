package com.dicsar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ResultadoProductoDTO;
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
    public ResponseEntity<ResultadoProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
    	ResultadoProductoDTO resultado = productoService.guardar(dto, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultadoProductoDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ProductoDTO dto) {
        ResultadoProductoDTO resultado = productoService.actualizar(id, dto, "admin");
        return ResponseEntity.ok(resultado);
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