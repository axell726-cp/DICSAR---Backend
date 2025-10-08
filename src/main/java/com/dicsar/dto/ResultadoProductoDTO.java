package com.dicsar.dto;

import java.util.List;

import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoProductoDTO {
	 private Producto producto;
	 private List<Notificacion> alertas;
}
