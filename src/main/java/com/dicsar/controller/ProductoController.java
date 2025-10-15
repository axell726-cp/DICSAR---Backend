package com.dicsar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ResultadoProductoDTO;
import com.dicsar.entity.HistorialPrecio;
import com.dicsar.entity.Producto;
import com.dicsar.enums.EstadoVencimiento;
import com.dicsar.service.HistorialPrecioService;
import com.dicsar.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final HistorialPrecioService historialPrecioService;

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
    
    @PatchMapping("/{id}/precio")
    public ResponseEntity<String> actualizarPrecio(@PathVariable Long id,
                                                   @RequestParam Double nuevoPrecio,
                                                   @RequestParam String usuario) {
        productoService.actualizarSoloPrecio(id, nuevoPrecio, usuario);
        return ResponseEntity.ok("Precio actualizado correctamente");
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<String> actualizarEstado(@PathVariable Long id,
                                                   @RequestParam boolean nuevoEstado,
                                                   @RequestParam String usuario) {
        productoService.actualizarSoloEstado(id, nuevoEstado, usuario);
        return ResponseEntity.ok("Estado del producto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        productoService.eliminarConRegla(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
    
    @GetMapping("/{id}/historial-precios")
    public ResponseEntity<List<HistorialPrecio>> obtenerHistorialPrecios(@PathVariable Long id) {
        List<HistorialPrecio> historial = historialPrecioService.obtenerHistorialPorProducto(id);
        return historial.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(historial);
    }
    
    @GetMapping("/stock")
    public ResponseEntity<List<Producto>> filtrarStock(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) EstadoVencimiento estadoVencimiento,
            @RequestParam(required = false) Integer stockMin,
            @RequestParam(required = false) Integer stockMax) {

        // Validación de filtros vacíos
        if (categoriaId == null && proveedorId == null && estadoVencimiento == null
                && stockMin == null && stockMax == null) {
            throw new IllegalArgumentException(
                "Debe especificar al menos un parámetro de filtrado (categoría, proveedor, estado de vencimiento o rango de stock)."
            );
        }

        List<Producto> productos = productoService.filtrarStock(
                categoriaId, proveedorId, estadoVencimiento, stockMin, stockMax);

        // Devolver 204 si no hay resultados
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(productos);
    }

}