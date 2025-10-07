 package com.dicsar.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
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
    private String descripcion;
    private Double precio;
    private Integer stock;
    
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

    private Double precioCompra;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "historial_precios", joinColumns = @JoinColumn(name = "id_producto"))
    @OrderBy("fechaCambio ASC")
    private List<CambioPrecio> historialCambios = new ArrayList<>();
    
    public Producto copiaLigera() {
        return Producto.builder()
            .idProducto(idProducto)
            .nombre(nombre)
            .descripcion(descripcion)
            .precio(precio)
            .stock(stock)
            .estado(estado)
            .categoria(categoria)
            .precioCompra(precioCompra)
            .build();
    }

}
