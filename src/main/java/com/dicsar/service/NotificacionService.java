package com.dicsar.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoAlerta;
import com.dicsar.repository.NotificacionRepository;
import com.dicsar.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final ProductoRepository productoRepository;

    // 🔹 Método para generar alertas según vencimiento
    public List<Notificacion> generarAlertasPorVencimiento(Producto producto, String usuario) {
        List<Notificacion> alertas = new ArrayList<>();

        if (producto.getFechaVencimiento() != null) {
            LocalDateTime hoy = LocalDateTime.now();
            LocalDateTime venc = producto.getFechaVencimiento();

            long diasRestantes = ChronoUnit.DAYS.between(hoy, venc);

            Notificacion alerta = null;

            if (diasRestantes < 0) {
                alerta = Notificacion.builder()
                        .producto(producto)
                        .tipo(TipoAlerta.CRITICA)
                        .descripcion("El producto '" + producto.getNombre() +
                                "' ya venció el " + venc.toLocalDate())
                        .usuario(usuario)
                        .fechaHora(LocalDateTime.now())
                        .build();
            } else if (diasRestantes <= 10) {
                alerta = Notificacion.builder()
                        .producto(producto)
                        .tipo(TipoAlerta.ADVERTENCIA)
                        .descripcion("El producto '" + producto.getNombre() +
                                "' está próximo a vencer el " + venc.toLocalDate())
                        .usuario(usuario)
                        .fechaHora(LocalDateTime.now())
                        .build();
            }

            if (alerta != null) {
                notificacionRepository.save(alerta);
                alertas.add(alerta);
            }
        }

        return alertas;
    }

    // 🔹 Listar todas las notificaciones
    public List<Notificacion> listar() {
        return notificacionRepository.findAll();
    }

    // 🔹 Listar por producto
    public List<Notificacion> listarPorProducto(Long idProducto) {
        Producto p = productoRepository.findById(idProducto).orElseThrow();
        return notificacionRepository.findByProducto(p);
    }

    // 🔹 Tarea automática diaria (se ejecuta todos los días a las 02:00 AM)
    @Scheduled(cron = "0 0 2 * * *")
    public void revisarVencimientosAutomaticamente() {
        List<Producto> productos = productoRepository.findAll();

        for (Producto p : productos) {
            if (p.getFechaVencimiento() != null) {
                generarAlertasPorVencimiento(p, "sistema");
            }
        }
        System.out.println("✅ Revisión automática de vencimientos ejecutada: " + LocalDateTime.now());
    }
}
