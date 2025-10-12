package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dicsar.entity.Movimiento;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoMovimiento;
import com.dicsar.repository.MovimientoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

	private final MovimientoRepository movimientoRepository;

	public void registrarMovimiento(Producto producto, int stockAnterior, int stockNuevo, String usuario) {
		if (Objects.equals(stockAnterior, stockNuevo))
			return;

		int diferencia = stockNuevo - stockAnterior;

		TipoMovimiento tipo = diferencia > 0 ? TipoMovimiento.ENTRADA
				: diferencia < 0 ? TipoMovimiento.SALIDA : TipoMovimiento.AJUSTE;

		Movimiento mov = Movimiento.builder().producto(producto).tipoMovimiento(tipo).cantidad(Math.abs(diferencia))
				.descripcion("Cambio de stock: " + stockAnterior + " → " + stockNuevo).usuarioMovimiento(usuario)
				.fechaMovimiento(LocalDateTime.now()).build();

		movimientoRepository.save(mov);
	}

	@Transactional
	public Movimiento crearMovimiento(Movimiento movimiento, String usuario) {
		Producto producto = movimiento.getProducto();
		int stockActual = producto.getStockActual();
		int cantidad = movimiento.getCantidad();

		switch (movimiento.getTipoMovimiento()) {
		case ENTRADA -> producto.setStockActual(stockActual + cantidad);
		case SALIDA -> {
			if (stockActual < cantidad) {
				throw new IllegalArgumentException("Stock insuficiente para realizar la salida.");
			}
			producto.setStockActual(stockActual - cantidad);
		}
		case AJUSTE -> producto.setStockActual(cantidad);
		}

		producto.setFechaActualizacion(LocalDateTime.now());
		movimiento.setFechaMovimiento(LocalDateTime.now());
		return movimientoRepository.save(movimiento);
	}

	public List<Movimiento> listarTodos() {
		return movimientoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaMovimiento"));
	}

	public List<Movimiento> listarPorProducto(Long idProducto) {
		return movimientoRepository.findByProductoIdProductoOrderByFechaMovimientoDesc(idProducto);
	}

	public List<Movimiento> listarPorTipo(TipoMovimiento tipo) {
	    return movimientoRepository.findByTipoMovimiento(tipo);
	}

}
