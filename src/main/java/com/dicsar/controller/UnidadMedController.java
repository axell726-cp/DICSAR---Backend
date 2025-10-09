package com.dicsar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dicsar.entity.UnidadMed;
import com.dicsar.service.UnidadMedService;

@RestController
@RequestMapping("api/unidades-medida")
public class UnidadMedController {

    private final UnidadMedService unidadMedService;

    public UnidadMedController(UnidadMedService unidadMedService) {
        this.unidadMedService = unidadMedService;
    }

    @GetMapping
    public List<UnidadMed> listar() {
        return unidadMedService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMed> obtener(@PathVariable Long id) {
        return unidadMedService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UnidadMed> guardar(@RequestBody UnidadMed unidadMed) {
        UnidadMed guardada = unidadMedService.guardar(unidadMed);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMed> actualizar(@PathVariable Long id, @RequestBody UnidadMed unidadMed) {
        UnidadMed actualizada = unidadMedService.actualizar(id, unidadMed);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<UnidadMed> existente = unidadMedService.obtener(id);

        if (existente.isPresent()) {
            unidadMedService.eliminar(id);
            return ResponseEntity.ok("Unidad de medida eliminada correctamente.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
