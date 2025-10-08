 package com.dicsar.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;
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
    @JoinColumn(name = "id_categoria", nullable = false)
    @JsonBackReference
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMed unidadMedida;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = true)
    private Proveedor proveedor;

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

