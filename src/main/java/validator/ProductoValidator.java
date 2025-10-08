package validator;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.dicsar.dto.ProductoDTO;
import com.dicsar.entity.Producto;
import com.dicsar.repository.ProductoRepository;

@Component
public class ProductoValidator {
	
	private static final double MIN_PRECIO = 1.00;
    private static final double MAX_PRECIO = 500.00;

    private final ProductoRepository productoRepository;

    public ProductoValidator(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public void validar(ProductoDTO dto) {
        validar(dto, null); // Solo delega al método que ya existe, pasando null
    }

    public void validar(ProductoDTO dto, Long idProducto) {
        if (dto == null)
            throw new IllegalArgumentException("El producto no puede ser nulo.");

        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");

        validarPrecio(dto.getPrecioBase());

        if (dto.getStockActual() == null || dto.getStockActual() < 0)
            throw new IllegalArgumentException("El stock no puede ser negativo ni nulo.");

        Optional<Producto> productoConCodigo = productoRepository.findByCodigo(dto.getCodigo());
        if (productoConCodigo.isPresent() && !productoConCodigo.get().getIdProducto().equals(idProducto)) {
            throw new IllegalArgumentException("El código ya existe, debe ser único.");
        }

        if (dto.getCategoriaId() == null)
            throw new IllegalArgumentException("Debe asignarse una categoría al producto.");

        if (dto.getStockMinimo() == null || dto.getStockMinimo() < 0)
            throw new IllegalArgumentException("Debe definir un stock mínimo mayor o igual a 0.");

        Optional<Producto> productoConNombre = productoRepository.findByNombreAndCategoriaIdCategoria(dto.getNombre(), dto.getCategoriaId());
        if (productoConNombre.isPresent() && !productoConNombre.get().getIdProducto().equals(idProducto)) {
            throw new IllegalArgumentException("Ya existe un producto con el mismo nombre en esta categoría.");
        }

        if (dto.getUnidadMedidaId() == null)
            throw new IllegalArgumentException("Debe asignarse una unidad de medida al producto.");
    }

    public void validarPrecio(Double precio) {
        if (precio == null)
            throw new IllegalArgumentException("Debe establecerse un precio base.");

        if (precio <= 0)
            throw new IllegalArgumentException("No se permite registrar precios iguales o menores a cero.");

        if (precio < MIN_PRECIO || precio > MAX_PRECIO)
            throw new IllegalArgumentException(
                String.format("El precio debe estar entre S/%.2f y S/%.2f.", MIN_PRECIO, MAX_PRECIO)
            );
    }

    public void validarCambioEstado(Producto producto, boolean nuevoEstado) {
        if (!nuevoEstado && producto.getStockActual() > 0)
            throw new IllegalStateException("No se puede inactivar un producto con stock disponible.");

        if (nuevoEstado && producto.getFechaVencimiento() != null &&
                producto.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede activar un producto vencido.");
        }
    }
}
