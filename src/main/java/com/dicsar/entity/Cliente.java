package com.dicsar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    private String nombre;
    private String apellidos;

    // Tipo de documento: puede ser DNI o RUC
    private String tipoDocumento; // Ejemplo: "DNI" o "RUC"
    private String numeroDocumento;

    private String direccion;
    private String telefono;
    private String email;

    // Si el cliente es empresa, se guarda la razón social
    private String razonSocial;

    @Builder.Default
    private Boolean esEmpresa = false;

    @Builder.Default
    private Boolean estado = true;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}
