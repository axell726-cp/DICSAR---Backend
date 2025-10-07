package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dicsar.entity.Producto;
import com.dicsar.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar todos
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // Obtener uno
    public Optional<Producto> obtener(Long idProducto) {
        return productoRepository.findById(idProducto);
    }

    // Guardar con validaciones
    public Producto guardar(Producto producto) {
        validarProducto(producto);

        if (producto.getIdProducto() == null) {
            // Nuevo producto
            producto.setFechaCreacion(LocalDateTime.now());
            producto.setEstado(true);
        } else {
            // Actualización
            producto.setFechaActualizacion(LocalDateTime.now());
        }

        return productoRepository.save(producto);
    }

    // Eliminar con validaciones
    public void eliminar(Long idProducto) {
        Optional<Producto> productoOpt = productoRepository.findById(idProducto);

        if (!productoOpt.isPresent()) {
            throw new RuntimeException("Producto no encontrado");
        }

        Producto producto = productoOpt.get();

        // No eliminar si está activo
        if (Boolean.TRUE.equals(producto.getEstado())) {
            throw new RuntimeException("No se puede eliminar un producto activo. Desactívelo primero.");
        }

        productoRepository.deleteById(idProducto);
    }

    public Optional<Producto> getOne(Long idProducto) {
        return productoRepository.findById(idProducto);
    }

    /**
     * Validaciones de reglas de negocio
     */
    private void validarProducto(Producto producto) {
        // 1. Nombre obligatorio
        if (!StringUtils.hasText(producto.getNombre())) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        // 2. Código obligatorio
        if (!StringUtils.hasText(producto.getCodigo())) {
            throw new RuntimeException("El código del producto es obligatorio.");
        }

        // 3. Código único
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            // si ya existe otro con ese código y no es el mismo
            Optional<Producto> existente = productoRepository.findByCodigo(producto.getCodigo());
            if (existente.isPresent() && 
               (producto.getIdProducto() == null || 
                !existente.get().getIdProducto().equals(producto.getIdProducto()))) {
                throw new RuntimeException("El código ya existe, debe ser único.");
            }
        }

        // 4. Categoría obligatoria
        if (producto.getCategoria() == null) {
            throw new RuntimeException("El producto debe estar asociado a una categoría.");
        }

        // 5. Unidad de medida obligatoria
        if (producto.getUnidadMedida() == null) {
            throw new RuntimeException("El producto debe tener una unidad de medida.");
        }

        // 6. Proveedor obligatorio
        if (producto.getProveedor() == null) {
            throw new RuntimeException("El producto debe tener un proveedor.");
        }

        // 7. Precio válido (>0)
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new RuntimeException("El producto debe tener un precio base mayor a 0.");
        }

        // 8. Stock actual no puede ser negativo
        if (producto.getStockActual() != null && producto.getStockActual() < 0) {
            throw new RuntimeException("El stock actual no puede ser negativo.");
        }

        // 9. Stock mínimo obligatorio y válido
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            throw new RuntimeException("El producto debe tener un stock mínimo definido.");
        }

        // 10. Estado obligatorio
        if (producto.getEstado() == null) {
            throw new RuntimeException("El estado del producto es obligatorio (activo/inactivo).");
        }
    }
}
