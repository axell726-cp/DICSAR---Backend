package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ReglaPrecioDTO;
import com.dicsar.entity.Categoria;
import com.dicsar.entity.Producto;
import com.dicsar.enums.TipoRegla;
import com.dicsar.repository.CategoriaRepository;
import com.dicsar.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoService {
	
	 private final ProductoRepository productoRepository;
	 
	 private final CategoriaRepository categoriaRepository;

	    private static final double MAX_PRECIO = 10000;
	    
	    public ProductoService(ProductoRepository productoRepository,
                CategoriaRepository categoriaRepository) {
	    	this.productoRepository = productoRepository;
	    	this.categoriaRepository = categoriaRepository;
	    }

	    // ---- CRUD ----
	    public List<Producto> listar() {
	        return productoRepository.findAll();
	    }

	    public Producto obtenerPorId(Long id) {
	        return productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
	    }

	    public Producto guardar(Producto producto) {
	    	return productoRepository.save(producto);
	    }

	    public Producto guardarDTO(ProductoDTO dto) {
	        validarPrecio(dto.getPrecioBase());

	        Producto producto = Producto.builder()
	                .nombre(dto.getNombre())
	                .descripcion(dto.getDescripcion())
	                .precio(dto.getPrecioBase())
	                .stock(dto.getStock())
	                .estado(true)
	                .fechaCreacion(LocalDateTime.now())
	                .fechaActualizacion(LocalDateTime.now())
	                .build();
	        
	        Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
	                   .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	        		producto.setCategoria(cat);


	        return productoRepository.save(producto);
	    }

	    public Producto actualizar(Long id, ProductoDTO dto) {
	        Producto producto = obtenerPorId(id);
	        validarPrecio(dto.getPrecioBase());

	        producto.setNombre(dto.getNombre());
	        producto.setDescripcion(dto.getDescripcion());
	        producto.setPrecio(dto.getPrecioBase());
	        producto.setStock(dto.getStock());
	        producto.setFechaActualizacion(LocalDateTime.now());

	        return productoRepository.save(producto);
	    }

	    public void cambiarEstado(Long id, boolean nuevoEstado) {
	        Producto producto = obtenerPorId(id);

	        if (!nuevoEstado && producto.getStock() > 0) {
	            throw new IllegalStateException("No se puede inactivar un producto con stock disponible");
	        }

	        if (nuevoEstado && producto.getFechaVencimiento() != null &&
	                producto.getFechaVencimiento().isBefore(LocalDateTime.now())) {
	            throw new IllegalStateException("No se puede activar un producto vencido");
	        }

	        producto.setEstado(nuevoEstado);
	        producto.setFechaActualizacion(LocalDateTime.now());
	        productoRepository.save(producto);
	    }

	    public void eliminarConRegla(Long id) {
	        Producto producto = obtenerPorId(id);

	        if (Boolean.TRUE.equals(producto.getEstado())) {
	            throw new IllegalStateException("No se puede eliminar un producto activo. Primero cámbielo a inactivo.");
	        }

	        productoRepository.delete(producto);
	    }

	    // ---- Validaciones ----
	    private void validarPrecio(Double precio) {
	        if (precio == null || precio < 0) {
	            throw new IllegalArgumentException("El precio no puede ser negativo o nulo");
	        }
	        if (precio > MAX_PRECIO) {
	            throw new IllegalArgumentException("El precio no puede superar los " + MAX_PRECIO);
	        }
	    }

	    public void validarReglaPrecio(ReglaPrecioDTO regla) {
	        TipoRegla tipo = regla.getTipoRegla();

	        switch (tipo) {
	            case DESCUENTO_CANTIDAD -> {
	                if (regla.getCantidadMinima() == null || regla.getPorcentaje() == null) {
	                    throw new IllegalArgumentException("La regla por cantidad requiere cantidad mínima y porcentaje");
	                }
	            }
	            case CLIENTE_ESPECIAL -> {
	                if (regla.getClienteId() == null || regla.getPorcentaje() == null) {
	                    throw new IllegalArgumentException("La regla de cliente especial requiere clienteId y porcentaje");
	                }
	            }
	            case PROMOCION -> {
	                if (regla.getMonto() == null || regla.getFechaInicio() == null || regla.getFechaFin() == null) {
	                    throw new IllegalArgumentException("La promoción requiere monto fijo y fechas válidas");
	                }
	            }
	        }
	    }
}
