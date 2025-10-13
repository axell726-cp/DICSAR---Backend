 package com.dicsar.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dicsar.enums.EstadoVencimiento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producto")
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
    
    private LocalDate fechaVencimiento;
    
    @Enumerated(EnumType.STRING)
    private EstadoVencimiento estadoVencimiento;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMed unidadMedida;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = true)
    private Proveedor proveedor;

    private Double precioCompra;
    
    public Producto copiaLigera() {
        return Producto.builder()
            .idProducto(idProducto)
            .nombre(nombre)
            .descripcion(descripcion)
            .precio(precio)
            .stockActual(stockActual)
            .stockMinimo(stockMinimo)
            .estado(estado)
            .categoria(categoria)
            .unidadMedida(unidadMedida)
            .proveedor(proveedor)
            .precioCompra(precioCompra)
            .build();
    }
}

