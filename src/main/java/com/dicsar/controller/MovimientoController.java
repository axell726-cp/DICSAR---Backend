package com.dicsar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dicsar.entity.Movimiento;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoMovimiento;
import com.dicsar.service.MovimientoService;
import com.dicsar.service.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {
	
	private final MovimientoService movimientoService;
    private final ProductoService productoService;

    // Registrar un nuevo movimiento
    @PostMapping
    public ResponseEntity<Movimiento> crearMovimiento(@RequestBody Movimiento movimiento, 
                                                      @RequestParam String usuario) {
        // Validar que el producto exista
        Producto producto = productoService.obtenerPorId(movimiento.getProducto().getIdProducto());
        movimiento.setProducto(producto);

        // Registrar el movimiento
        movimiento.setUsuarioMovimiento(usuario);
        Movimiento nuevoMovimiento = movimientoService.crearMovimiento(movimiento, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }

    // Listar todos los movimientos
    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos() {
        List<Movimiento> movimientos = movimientoService.listarTodos();
        return ResponseEntity.ok(movimientos);
    }

    // Listar movimientos de un producto específico
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<Movimiento>> listarPorProducto(@PathVariable Long idProducto) {
        productoService.obtenerPorId(idProducto); // valida que el producto exista
        List<Movimiento> movimientos = movimientoService.listarPorProducto(idProducto);
        return ResponseEntity.ok(movimientos);
    }

    // Filtrar movimientos por tipo
    @GetMapping("/tipo")
    public ResponseEntity<List<Movimiento>> filtrarPorTipo(@RequestParam TipoMovimiento tipo) {
        List<Movimiento> movimientos = movimientoService.listarPorTipo(tipo);
        return ResponseEntity.ok(movimientos);
    }
}
