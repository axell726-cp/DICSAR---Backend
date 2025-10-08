package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ResultadoProductoDTO;
import com.dicsar.entity.CambioPrecio;
import com.dicsar.entity.Categoria;
import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.entity.Proveedor;
import com.dicsar.entity.UnidadMed;
import com.dicsar.repository.CategoriaRepository;
import com.dicsar.repository.ProductoRepository;
import com.dicsar.repository.ProveedorRepository;
import com.dicsar.repository.UnidadMedRepository;
import com.dicsar.validator.ProductoValidator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;
	private final ProveedorRepository proveedorRepository;
    private final UnidadMedRepository unidadMedidaRepository;
    private final ReglaPrecioService reglaPrecioService;
	private final ProductoValidator productoValidator;
	    
	 public List<Producto> listar() {
	        return productoRepository.findAll();
	    }

	    public Producto obtenerPorId(Long id) {
	        return productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
	    }

	    public ResultadoProductoDTO guardar(ProductoDTO dto, String usuario) {
	        productoValidator.validar(dto);

	        Producto producto = construirProductoDesdeDTO(dto);
	        producto.setFechaCreacion(LocalDateTime.now());
	        producto.setFechaActualizacion(LocalDateTime.now());
	        producto.setEstado(true);

	        productoRepository.save(producto);

	        List<Notificacion> alertas = List.of();

	        return new ResultadoProductoDTO(producto, alertas);
	    }

	    public ResultadoProductoDTO actualizar(Long id, ProductoDTO dto, String usuario) {
	        productoValidator.validar(dto, id);

	        Producto producto = obtenerPorId(id);
	        Producto anterior = producto.copiaLigera();

	        // Detectar cambios de precio y registrar histórico
	        if (!Objects.equals(producto.getPrecio(), dto.getPrecioBase())) {
	            if (producto.getHistorialCambios() == null)
	                producto.setHistorialCambios(new ArrayList<>());

	            producto.getHistorialCambios().add(
	                new CambioPrecio(LocalDateTime.now(), producto.getPrecio(), dto.getPrecioBase())
	            );
	        }

	        // Actualizar datos básicos
	        producto.setNombre(dto.getNombre());
	        producto.setDescripcion(dto.getDescripcion());
	        producto.setCodigo(dto.getCodigo());
	        producto.setPrecio(dto.getPrecioBase());
	        producto.setStockActual(dto.getStockActual());
	        producto.setStockMinimo(dto.getStockMinimo());
	        producto.setFechaActualizacion(LocalDateTime.now());
	        producto.setCategoria(obtenerCategoria(dto.getCategoriaId()));
	        producto.setProveedor(obtenerProveedor(dto.getProveedorId()));
	        producto.setUnidadMedida(obtenerUnidad(dto.getUnidadMedidaId()));

	        List<Notificacion> alertas = reglaPrecioService.evaluarCambios(anterior, producto, usuario);

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

	    // -------------------------------------------------------------
	    // ---------------------- MÉTODOS PRIVADOS ---------------------
	    // -------------------------------------------------------------

	    private Producto construirProductoDesdeDTO(ProductoDTO dto) {
	        Categoria categoria = obtenerCategoria(dto.getCategoriaId());
	        Proveedor proveedor = obtenerProveedor(dto.getProveedorId());
	        UnidadMed unidad = obtenerUnidad(dto.getUnidadMedidaId());

	        return Producto.builder()
	                .nombre(dto.getNombre())
	                .descripcion(dto.getDescripcion())
	                .codigo(dto.getCodigo())
	                .precio(dto.getPrecioBase())
	                .stockActual(dto.getStockActual())
	                .stockMinimo(dto.getStockMinimo())
	                .categoria(categoria)
	                .proveedor(proveedor)
	                .unidadMedida(unidad)
	                .build();
	    }

	    private Categoria obtenerCategoria(Long id) {
	        return categoriaRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	    }

	    private Proveedor obtenerProveedor(Long id) {
	    	if (id == null) {
	            return null; 
	        }
	        return proveedorRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
	    }

	    private UnidadMed obtenerUnidad(Long id) {
	        return unidadMedidaRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));
	    }
}
