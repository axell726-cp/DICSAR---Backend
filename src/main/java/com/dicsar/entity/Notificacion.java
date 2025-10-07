package com.dicsar.entity;

import java.time.LocalDateTime;

import com.dicsar.enums.TipoAlerta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
	private Producto producto;
    private TipoAlerta tipo;
    private String descripcion;
    private LocalDateTime fechaHora;
}
