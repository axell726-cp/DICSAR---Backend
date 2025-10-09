package com.dicsar.dto;

import java.util.List;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultadoProductoDTO {
	 private Producto producto;
	 private List<Notificacion> alertas;
}
