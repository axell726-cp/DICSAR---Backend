package com.dicsar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambioPrecio {
	private LocalDateTime fechaCambio;
    private Double precioAnterior;
    private Double precioNuevo;
}
