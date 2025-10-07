package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoAlerta;

@Service
public class ReglaPrecioService {
	
    private static final double MIN_PRECIO = 1.00;
    private static final double MAX_PRECIO = 500.00;

    public List<Notificacion> evaluarCambios(Producto anterior, Producto actualizado) {
        List<Notificacion> alertas = new ArrayList<>();

        double precioAnt = anterior.getPrecio();
        double precioNuevo = actualizado.getPrecio();

        if (precioAnt > 0) {
            double variacion = Math.abs((precioNuevo - precioAnt) / precioAnt) * 100;
            if (variacion >= 10) {
                alertas.add(crearNotificacion(
                        actualizado,
                        TipoAlerta.ADVERTENCIA,
                        String.format("Cambio de precio significativo: %.2f%% (de %.2f a %.2f)", variacion, precioAnt, precioNuevo)
                ));
            }
        }

        if (actualizado.getPrecioCompra() != null && precioNuevo < actualizado.getPrecioCompra()) {
            alertas.add(crearNotificacion(
                    actualizado,
                    TipoAlerta.CRITICA,
                    String.format("El precio de venta (%.2f) es menor al costo de compra (%.2f)", precioNuevo, actualizado.getPrecioCompra())
            ));
        }

        if (actualizado.getHistorialCambios() != null && actualizado.getHistorialCambios().size() > 3) {
            LocalDateTime hace7dias = LocalDateTime.now().minusDays(7);
            long recientes = actualizado.getHistorialCambios().stream()
                    .filter(c -> c.getFechaCambio().isAfter(hace7dias))
                    .count();
            if (recientes > 3) {
                alertas.add(crearNotificacion(
                        actualizado,
                        TipoAlerta.INFORMATIVA,
                        "El producto ha sido actualizado más de 3 veces en los últimos 7 días."
                ));
            }
        }

        if (precioNuevo < MIN_PRECIO || precioNuevo > MAX_PRECIO) {
            alertas.add(crearNotificacion(
                    actualizado,
                    TipoAlerta.CRITICA,
                    String.format("El precio %.2f está fuera del rango permitido (%.2f - %.2f)", precioNuevo, MIN_PRECIO, MAX_PRECIO)
            ));
        }

        if (precioNuevo <= 0) {
            alertas.add(crearNotificacion(
                    actualizado,
                    TipoAlerta.CRITICA,
                    "No se permite registrar precios iguales o menores a cero."
            ));
        }

        return alertas;
    }

    private Notificacion crearNotificacion(Producto producto, TipoAlerta tipo, String descripcion) {
        return Notificacion.builder()
                .producto(producto)
                .tipo(tipo)
                .descripcion(descripcion)
                .fechaHora(LocalDateTime.now())
                .build();
    }
}
