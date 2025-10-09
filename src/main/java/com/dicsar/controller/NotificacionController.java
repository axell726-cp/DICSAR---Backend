package com.dicsar.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.dicsar.entity.Notificacion;
import com.dicsar.service.NotificacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // 🔹 Obtener todas las notificaciones
    @GetMapping
    public List<Notificacion> listar() {
        return notificacionService.listar();
    }

    // 🔹 Obtener notificaciones por producto
    @GetMapping("/producto/{idProducto}")
    public List<Notificacion> listarPorProducto(@PathVariable Long idProducto) {
        return notificacionService.listarPorProducto(idProducto);
    }
}
