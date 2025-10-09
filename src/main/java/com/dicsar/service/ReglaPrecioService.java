package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.enums.NivelAlerta;
import com.dicsar.enums.TipoAlerta;

@Service
public class ReglaPrecioService {
	
	private static final double MIN_PRECIO = 1.00;
    private static final double MAX_PRECIO = 500.00;
    private static final double VARIACION_UMBRAL = 10.0; // ±10%

    private final NotificacionService notificacionService;

    public ReglaPrecioService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public List<Notificacion> evaluarCambios(Producto anterior, Producto actualizado, String usuario) {
        List<Notificacion> alertas = new ArrayList<>();

        if (anterior == null || actualizado == null) return alertas;

        double precioAnt = anterior.getPrecio();
        double precioNuevo = actualizado.getPrecio();

        // 1️⃣ Cambio de precio significativo
        if (precioAnt > 0) {
            double variacion = Math.abs((precioNuevo - precioAnt) / precioAnt) * 100;
            if (variacion >= VARIACION_UMBRAL) {
                alertas.add(crearNotificacion(actualizado, TipoAlerta.ALERTA_PRECIO, NivelAlerta.ADVERTENCIA,
                        String.format("Cambio de precio significativo: %.2f%% (de %.2f a %.2f)",
                                variacion, precioAnt, precioNuevo),
                        usuario));
            }
        }

        // 2️⃣ Precio menor al costo de compra
        if (actualizado.getPrecioCompra() != null && precioNuevo < actualizado.getPrecioCompra()) {
            alertas.add(crearNotificacion(actualizado, TipoAlerta.ALERTA_PRECIO, NivelAlerta.CRITICA,
                    String.format("El precio de venta (%.2f) es menor al costo de compra (%.2f).",
                            precioNuevo, actualizado.getPrecioCompra()),
                    usuario));
        }

        // 3️⃣ Cambios frecuentes (más de 3 en los últimos 7 días)
        if (actualizado.getHistorialCambios() != null) {
            LocalDateTime hace7dias = LocalDateTime.now().minusDays(7);
            long recientes = actualizado.getHistorialCambios().stream()
                    .filter(c -> c.getFechaCambio().isAfter(hace7dias))
                    .count();
            if (recientes > 3) {
                alertas.add(crearNotificacion(actualizado, TipoAlerta.ALERTA_PRECIO, NivelAlerta.INFORMATIVA,
                        "El producto ha sido actualizado más de 3 veces en los últimos 7 días.",
                        usuario));
            }
        }

        // 4️⃣ Precio fuera de rango
        if (precioNuevo < MIN_PRECIO || precioNuevo > MAX_PRECIO) {
            alertas.add(crearNotificacion(actualizado, TipoAlerta.ALERTA_PRECIO, NivelAlerta.CRITICA,
                    String.format("El precio %.2f está fuera del rango permitido (%.2f - %.2f).",
                            precioNuevo, MIN_PRECIO, MAX_PRECIO),
                    usuario));
        }

        // 🔄 Persistir las notificaciones generadas
        if (!alertas.isEmpty()) {
        	alertas.forEach(notificacionService::guardar);
        }

        return alertas;
    }

    private Notificacion crearNotificacion(
            Producto producto,
            TipoAlerta tipo,
            NivelAlerta nivel,
            String descripcion,
            String usuario) {

        String titulo = switch (nivel) {
            case CRITICA -> "Alerta crítica de precios";
            case ADVERTENCIA -> "Advertencia de precios";
            case INFORMATIVA -> "Notificación informativa";
        };

        return Notificacion.builder()
                .producto(producto)
                .tipo(tipo)
                .nivel(nivel)
                .titulo(titulo)
                .descripcion(descripcion)
                .mensaje(descripcion)
                .usuario(usuario)
                .fechaHora(LocalDateTime.now())
                .build();
    }

}
