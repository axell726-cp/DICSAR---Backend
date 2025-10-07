package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ResultadoProductoDTO;
import com.dicsar.entity.CambioPrecio;
import com.dicsar.entity.Categoria;
import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.repository.CategoriaRepository;
import com.dicsar.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import validator.ProductoValidator;

@Service
public class ProductoService {
	
	 private final ProductoRepository productoRepository;
	 
	 private final CategoriaRepository categoriaRepository;
	 
	 private final ReglaPrecioService reglaPrecioService;
	 
	 private final ProductoValidator productoValidator;
	    
	 public ProductoService(
	            ProductoRepository productoRepository,
	            CategoriaRepository categoriaRepository,
	            ReglaPrecioService reglaPrecioService,
	            ProductoValidator productoValidator) {
	        this.productoRepository = productoRepository;
	        this.categoriaRepository = categoriaRepository;
	        this.reglaPrecioService = reglaPrecioService;
	        this.productoValidator = productoValidator;
	    }

	    // ---- CRUD ----
	    public List<Producto> listar() {
	        return productoRepository.findAll();
	    }

	    public Producto obtenerPorId(Long id) {
	        return productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
	    }

	    public ResultadoProductoDTO guardar(ProductoDTO dto) {
	        productoValidator.validar(dto);

	        Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
	                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

	        Producto producto = Producto.builder()
	                .nombre(dto.getNombre())
	                .descripcion(dto.getDescripcion())
	                .precio(dto.getPrecioBase())
	                .stock(dto.getStock())
	                .categoria(cat)
	                .estado(true)
	                .fechaCreacion(LocalDateTime.now())
	                .fechaActualizacion(LocalDateTime.now())
	                .build();

	        productoRepository.save(producto);

	        return new ResultadoProductoDTO(producto, List.of());
	    }

	    public ResultadoProductoDTO actualizar(Long id, ProductoDTO dto) {
	        productoValidator.validar(dto);

	        Producto producto = obtenerPorId(id);
	        Producto anterior = producto.copiaLigera();

	        if (!producto.getPrecio().equals(dto.getPrecioBase())) {
	            if (producto.getHistorialCambios() == null)
	                producto.setHistorialCambios(new ArrayList<>());

	            producto.getHistorialCambios().add(
	                new CambioPrecio(LocalDateTime.now(), producto.getPrecio(), dto.getPrecioBase())
	            );
	        }

	        producto.setNombre(dto.getNombre());
	        producto.setDescripcion(dto.getDescripcion());
	        producto.setPrecio(dto.getPrecioBase());
	        producto.setStock(dto.getStock());
	        producto.setFechaActualizacion(LocalDateTime.now());

	        List<Notificacion> alertas = reglaPrecioService.evaluarCambios(anterior, producto);

	        productoRepository.save(producto);

	        return new ResultadoProductoDTO(producto, alertas);
	    }

	    public void cambiarEstado(Long id, boolean nuevoEstado) {
	        Producto producto = obtenerPorId(id);

	        productoValidator.validarCambioEstado(producto, nuevoEstado);

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
}
