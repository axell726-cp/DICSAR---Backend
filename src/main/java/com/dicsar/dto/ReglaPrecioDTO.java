package com.dicsar.dto;

import java.time.LocalDateTime;

import com.dicsar.enums.TipoRegla;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReglaPrecioDTO {
	
	private Long id;

    private Long productoId; // Relación al producto
    
    @NotNull(message = "El tipo de regla es obligatorio")
    private TipoRegla tipoRegla; 		//1 de los enums/TipoRegla

    private Double porcentaje;   		// Ej: 10% de descuento
    private Double monto;        		// Ej: S/ 5.00 de descuento

    private Integer cantidadMinima; 	// Aplica solo si es DESCUENTO_CANTIDAD

    private Long clienteId;      		// Aplica solo si es CLIENTE_ESPECIAL

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
