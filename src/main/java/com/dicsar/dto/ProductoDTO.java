package com.dicsar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

	private Long idProducto;
	
	@NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    
    private String codigo;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "500.00", message = "El precio no puede ser mayor a 500")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    private Double precioBase;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;
    
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
    
    private Long proveedorId;
    
    @NotNull(message = "La unidad de medida es obligatoria")
    private Long unidadMedidaId;
    private Double precioCompra;
    private LocalDateTime fechaVencimiento;


}
