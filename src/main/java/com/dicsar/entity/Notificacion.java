package com.dicsar.entity;

import java.time.LocalDateTime;

import com.dicsar.enums.NivelAlerta;
import com.dicsar.enums.TipoAlerta;

import jakarta.persistence.Column;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notificacion")
public class Notificacion {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String titulo;
	
	@Column(length = 500)
	private String mensaje;
	
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;
    
    @Enumerated(EnumType.STRING)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 300)
    private String descripcion;
    
    @Column(nullable = false, length = 100)
    private String usuario;

    @Builder.Default
    private LocalDateTime fechaHora = LocalDateTime.now();
}
