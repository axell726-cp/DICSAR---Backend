package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dicsar.dto.NotificacionDTO;
import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.enums.NivelAlerta;
import com.dicsar.enums.TipoAlerta;
import com.dicsar.repository.NotificacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService {
	
	private final NotificacionRepository notificacionRepository;
	
	public boolean existeNotificacionActiva(Long productoId, TipoAlerta tipo) {
	    if (productoId == null) return false;
	    
	    return notificacionRepository.existsByProductoIdProductoAndTipo(productoId, tipo);
	}

	public void notificarEvento(Producto producto, 
            TipoAlerta tipo, 
            NivelAlerta nivel, 
            String titulo, 
            String mensaje, 
            String descripcion, 
            String usuario) {

		Notificacion n = Notificacion.builder()
		.titulo(titulo)
		.mensaje(mensaje)
		.tipo(tipo)
		.nivel(nivel)
		.descripcion(descripcion)
		.usuario(usuario)
		.fechaHora(LocalDateTime.now())
		.producto(producto)
		.build();
		
		notificacionRepository.save(n);
	}

	public Notificacion notificarVencimientoProximo(Producto producto, long dias, String usuario) {
	    Notificacion n = Notificacion.builder()
	        .titulo("Producto próximo a vencer")
	        .mensaje("El producto " + producto.getNombre() + " vencerá en " + dias + " días.")
	        .tipo(TipoAlerta.ALERTA_VENCIMIENTO)
	        .nivel(NivelAlerta.ADVERTENCIA)
	        .descripcion("Producto dentro de los próximos 30 días de vencimiento.")
	        .usuario(usuario)
	        .fechaHora(LocalDateTime.now())
	        .producto(producto)
	        .build();

	    notificacionRepository.save(n);
	    return n;
	}

	public Notificacion notificarVencimientoExpirado(Producto producto, String usuario) {
	    Notificacion n = Notificacion.builder()
	        .titulo("Producto vencido")
	        .mensaje("El producto " + producto.getNombre() + " ha vencido y no puede ser comercializado.")
	        .tipo(TipoAlerta.ALERTA_VENCIMIENTO)
	        .nivel(NivelAlerta.CRITICA)
	        .descripcion("Producto vencido detectado por el sistema.")
	        .usuario(usuario)
	        .fechaHora(LocalDateTime.now())
	        .producto(producto)
	        .build();

	    notificacionRepository.save(n);
	    return n;
	}

	public Notificacion notificarStockMinimo(Producto producto, String usuario) {
	    Notificacion n = Notificacion.builder()
	        .titulo("Stock mínimo alcanzado")
	        .mensaje("El producto " + producto.getNombre() + " ha alcanzado su stock mínimo.")
	        .tipo(TipoAlerta.STOCK_BAJO)
	        .nivel(NivelAlerta.ADVERTENCIA)
	        .descripcion("El stock actual es igual o menor al stock mínimo configurado.")
	        .usuario(usuario)
	        .fechaHora(LocalDateTime.now())
	        .producto(producto)
	        .build();

	    notificacionRepository.save(n);
	    return n;
	}
    
    public List<NotificacionDTO> mapearADTO(List<Notificacion> notificaciones) {
        return notificaciones.stream()
            .map(n -> NotificacionDTO.builder()
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .descripcion(n.getDescripcion())
                .tipo(n.getTipo())
                .nivel(n.getNivel())
                .fechaHora(n.getFechaHora())
                .build())
            .collect(Collectors.toList());
    }
    
    public void guardar(Notificacion notificacion) {
        notificacionRepository.save(notificacion);
    }

}
