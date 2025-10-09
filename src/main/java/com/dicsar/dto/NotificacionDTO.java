package com.dicsar.dto;

import java.time.LocalDateTime;

import com.dicsar.enums.NivelAlerta;
import com.dicsar.enums.TipoAlerta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacionDTO {
	private String titulo;
    private String mensaje;
    private String descripcion;
    private TipoAlerta tipo;
    private NivelAlerta nivel;
    private LocalDateTime fechaHora;
}
