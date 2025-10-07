package com.dicsar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
import jakarta.persistence.Entity;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Proveedor {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Proveedor;
    private String nombre;
    private String apellidos;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String ruc;
    
    @Builder.Default
    private Boolean estado = true;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();


}
