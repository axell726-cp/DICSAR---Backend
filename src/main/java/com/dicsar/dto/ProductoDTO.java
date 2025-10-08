package com.dicsar.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoDTO {

	private Long idProducto;
	
	@NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    
    private String codigo;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "10000.0", message = "El precio no puede ser mayor a 10,000")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    private Double precioBase;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;
    private Integer stockMinimo;
    
    private Long categoriaId;
    private Long proveedorId;
    private Long unidadMedidaId;


}
