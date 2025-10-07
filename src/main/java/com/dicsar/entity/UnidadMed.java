package com.dicsar.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UnidadMed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidadMed;

    private String nombre;       // Ej: Kilogramos, Litros, Unidades
    private String abreviatura;  // Ej: kg, L, und

    @Builder.Default
    private Boolean estado = true;
}
