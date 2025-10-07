package validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import com.dicsar.dto.ProductoDTO;
import com.dicsar.entity.Producto;

@Component
public class ProductoValidator {
	
	public void validar(ProductoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        validarPrecio(dto.getPrecioBase());

        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo ni nulo.");
        }
    }

    public void validarPrecio(Double precio) {
        if (precio == null || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
    }

    public void validarCambioEstado(Producto producto, boolean nuevoEstado) {
        if (!nuevoEstado && producto.getStock() > 0) {
            throw new IllegalStateException("No se puede inactivar un producto con stock disponible.");
        }

        if (nuevoEstado && producto.getFechaVencimiento() != null &&
                producto.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede activar un producto vencido.");
        }
    }
}
