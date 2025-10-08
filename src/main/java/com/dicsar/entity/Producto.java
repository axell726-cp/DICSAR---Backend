package com.dicsar.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    private String nombre;
    private String codigo;
    private String descripcion;
    private Double precio;
    private Integer stockMinimo;
    private Integer stockActual;
    
    @Builder.Default
    private Boolean estado = true;
    
    private LocalDateTime fechaVencimiento;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    @JsonBackReference
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMed unidadMedida;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;
}
