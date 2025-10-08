package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ReglaPrecioDTO;
import com.dicsar.entity.Categoria;
import com.dicsar.entity.Producto;
import com.dicsar.entity.Proveedor;
import com.dicsar.entity.UnidadMed;
import com.dicsar.enums.TipoRegla;
import com.dicsar.repository.CategoriaRepository;
import com.dicsar.repository.ProductoRepository;
import com.dicsar.repository.ProveedorRepository;
import com.dicsar.repository.UnidadMedRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;
	private final ProveedorRepository proveedorRepository;
    private final UnidadMedRepository unidadMedidaRepository;
	private static final double MAX_PRECIO = 10000;
	    
	    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, ProveedorRepository proveedorRepository,
	    		UnidadMedRepository unidadMedidaRepository) {
	    	this.productoRepository = productoRepository;
	    	this.categoriaRepository = categoriaRepository;
	    	this.proveedorRepository = proveedorRepository;
	        this.unidadMedidaRepository = unidadMedidaRepository;
	    }
	    
	 // ---- CRUD ----

	    public List<Producto> listar() {
	        return productoRepository.findAll();
	    }

	    public Optional<Producto> obtener(Long id) {
	        return productoRepository.findById(id);
	    }

	    public Producto guardarDTO(ProductoDTO dto) {
	        validarCamposDTO(dto);
	        validarPrecio(dto.getPrecioBase());

	        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
	                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	        
	        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
	        	    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
	        
	        UnidadMed unidad = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
	        	    .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));

	        Producto producto = Producto.builder()
	                .nombre(dto.getNombre())
	                .descripcion(dto.getDescripcion())
	                .codigo(dto.getCodigo())
	                .precio(dto.getPrecioBase())
	                .stockActual(dto.getStockActual())
	                .stockMinimo(dto.getStockMinimo())
	                .estado(true)
	                .categoria(categoria)
	                .proveedor(proveedor)
	                .unidadMedida(unidad)
	                .fechaCreacion(LocalDateTime.now())
	                .fechaActualizacion(LocalDateTime.now())
	                .build();

	        return productoRepository.save(producto);
	    }

	    public Producto actualizar(Long id, ProductoDTO dto) {
	        Producto producto = productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

	        validarCamposDTO(dto);
	        validarPrecio(dto.getPrecioBase());

	        producto.setNombre(dto.getNombre());
	        producto.setDescripcion(dto.getDescripcion());
	        producto.setCodigo(dto.getCodigo());
	        producto.setPrecio(dto.getPrecioBase());
	        producto.setStockActual(dto.getStockActual());
	        producto.setStockMinimo(dto.getStockMinimo());
	        producto.setFechaActualizacion(LocalDateTime.now());

	        if (dto.getCategoriaId() != null) {
	            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
	                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	            producto.setCategoria(categoria);
	        }
	        
	        if (dto.getProveedorId() != null) {
	            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
	                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
	            producto.setProveedor(proveedor);
	        }

	        if (dto.getUnidadMedidaId() != null) {
	            UnidadMed unidad = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
	                    .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));
	            producto.setUnidadMedida(unidad);
	        }


	        return productoRepository.save(producto);
	    }

	    public void cambiarEstado(Long id, boolean nuevoEstado) {
	        Producto producto = productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

	        if (!nuevoEstado && producto.getStockActual() > 0) {
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
	        Producto producto = productoRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

	        if (Boolean.TRUE.equals(producto.getEstado())) {
	            throw new IllegalStateException("No se puede eliminar un producto activo. Primero cámbielo a inactivo.");
	        }

	        productoRepository.delete(producto);
	    }

	    // ---- Validaciones ----

	    private void validarCamposDTO(ProductoDTO dto) {
	        if (!StringUtils.hasText(dto.getNombre())) {
	            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
	        }

	        if (!StringUtils.hasText(dto.getCodigo())) {
	            throw new IllegalArgumentException("El código del producto es obligatorio.");
	        }

	        Optional<Producto> existente = productoRepository.findByCodigo(dto.getCodigo());
	        if (existente.isPresent() && (dto.getIdProducto() == null ||
	                !existente.get().getIdProducto().equals(dto.getIdProducto()))) {
	            throw new IllegalArgumentException("El código ya existe, debe ser único.");
	        }

	        if (dto.getPrecioBase() == null || dto.getPrecioBase() <= 0) {
	            throw new IllegalArgumentException("El producto debe tener un precio base mayor a 0.");
	        }

	        if (dto.getStockActual() != null && dto.getStockActual() < 0) {
	            throw new IllegalArgumentException("El stock no puede ser negativo.");
	        }
	    }

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
