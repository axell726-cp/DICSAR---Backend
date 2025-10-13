package com.dicsar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.dicsar.dto.ProductoDTO;
import com.dicsar.dto.ResultadoProductoDTO;
import com.dicsar.entity.Categoria;
import com.dicsar.entity.HistorialPrecio;
import com.dicsar.entity.Notificacion;
import com.dicsar.entity.Producto;
import com.dicsar.entity.Proveedor;
import com.dicsar.entity.UnidadMed;
import com.dicsar.enums.EstadoVencimiento;
import com.dicsar.enums.TipoAlerta;
import com.dicsar.repository.CategoriaRepository;
import com.dicsar.repository.HistorialPrecioRepository;
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
    private final HistorialPrecioRepository historialPrecioRepository;
    private final ReglaPrecioService reglaPrecioService;
    private final ProductoValidator productoValidator;
    private final NotificacionService notificacionService;
    private final MovimientoService movimientoService;

    // 🔹 Listar todos los productos
    public List<Producto> listar() {
        List<Producto> productos = productoRepository.findAll();
        productos.forEach(p -> {
            if (p.getFechaVencimiento() != null) {
                p.setEstadoVencimiento(ProductoValidator.calcularEstadoVencimiento(p.getFechaVencimiento()));
            }
        });
        return productos;
    }

    // 🔹 Obtener producto por ID
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }

    // 🔹 Guardar producto nuevo
    public ResultadoProductoDTO guardar(ProductoDTO dto, String usuario) {
        productoValidator.validar(dto);
        productoValidator.validarStock(dto);
        validarFechaVencimiento(dto.getFechaVencimiento());

        Producto producto = construirProductoDesdeDTO(dto);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setEstado(true);
        producto.setEstadoVencimiento(ProductoValidator.calcularEstadoVencimiento(dto.getFechaVencimiento()));

        productoRepository.save(producto);

        List<Notificacion> alertas = verificarAlertasProducto(producto, usuario);

        return new ResultadoProductoDTO(producto, alertas);
    }

    // 🔹 Actualizar producto existente
    public ResultadoProductoDTO actualizar(Long id, ProductoDTO dto, String usuario) {
        productoValidator.validar(dto, id);
        productoValidator.validarStock(dto);
        validarFechaVencimiento(dto.getFechaVencimiento());

        Producto producto = obtenerPorId(id);
        Producto anterior = producto.copiaLigera();

        // Registrar cambio de precio
        registrarCambioPrecio(producto, dto.getPrecioBase(), usuario);

        // Actualizar datos del producto
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCodigo(dto.getCodigo());
        producto.setPrecio(dto.getPrecioBase());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setCategoria(obtenerCategoria(dto.getCategoriaId()));
        producto.setProveedor(obtenerProveedor(dto.getProveedorId()));
        producto.setUnidadMedida(obtenerUnidad(dto.getUnidadMedidaId()));
        producto.setEstadoVencimiento(ProductoValidator.calcularEstadoVencimiento(dto.getFechaVencimiento()));
        producto.setFechaActualizacion(LocalDateTime.now());

        productoRepository.save(producto);

        // Registrar movimiento si cambió el stock
        if (!Objects.equals(anterior.getStockActual(), producto.getStockActual())) {
            movimientoService.registrarMovimiento(producto, anterior.getStockActual(), producto.getStockActual(), usuario);
        }

        // Evaluar reglas y alertas
        List<Notificacion> alertas = reglaPrecioService.evaluarCambios(anterior, producto, usuario);
        alertas.addAll(verificarAlertasProducto(producto, usuario));

        return new ResultadoProductoDTO(producto, alertas);
    }

    // 🔹 Cambiar estado activo/inactivo
    public void cambiarEstado(Long id, boolean nuevoEstado) {
        Producto producto = obtenerPorId(id);
        productoValidator.validarCambioEstado(producto, nuevoEstado);
        producto.setEstado(nuevoEstado);
        producto.setFechaActualizacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    // 🔹 Eliminar producto si está inactivo
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

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .codigo(dto.getCodigo())
                .precio(dto.getPrecioBase())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .fechaVencimiento(dto.getFechaVencimiento())
                .categoria(categoria)
                .proveedor(proveedor)
                .unidadMedida(unidad)
                .precioCompra(dto.getPrecioCompra())
                .build();

        producto.setEstadoVencimiento(ProductoValidator.calcularEstadoVencimiento(dto.getFechaVencimiento()));
        return producto;
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

    private void validarFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento == null || !fechaVencimiento.isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha de vencimiento no puede ser menor o igual a la fecha actual.");
        }
    }

    // 🔹 Registrar histórico de precios
    private void registrarCambioPrecio(Producto producto, Double nuevoPrecio, String usuario) {
        if (!Objects.equals(producto.getPrecio(), nuevoPrecio)) {
            HistorialPrecio registro = HistorialPrecio.builder()
                    .producto(producto)
                    .precioAnterior(producto.getPrecio())
                    .precioNuevo(nuevoPrecio)
                    .usuario(usuario)
                    .fechaCambio(LocalDateTime.now())
                    .build();

            historialPrecioRepository.save(registro);
        }
    }

    // 🔹 Verificar alertas por vencimiento y stock
    private List<Notificacion> verificarAlertasProducto(Producto producto, String usuario) {
        List<Notificacion> alertas = new ArrayList<>();

        EstadoVencimiento estado = ProductoValidator.calcularEstadoVencimiento(producto.getFechaVencimiento());
        producto.setEstadoVencimiento(estado);

        if (!notificacionService.existeNotificacionActiva(producto.getIdProducto(), TipoAlerta.ALERTA_VENCIMIENTO)) {
            switch (estado) {
                case POR_VENCER -> alertas.add(
                        notificacionService.notificarVencimientoProximo(
                                producto,
                                ChronoUnit.DAYS.between(LocalDate.now(), producto.getFechaVencimiento()),
                                usuario
                        )
                );
                case VENCIDO -> alertas.add(
                        notificacionService.notificarVencimientoExpirado(producto, usuario)
                );
                default -> {}
            }
        }

        if (producto.getStockActual() <= producto.getStockMinimo()) {
            if (!notificacionService.existeNotificacionActiva(producto.getIdProducto(), TipoAlerta.STOCK_BAJO)) {
                alertas.add(notificacionService.notificarStockMinimo(producto, usuario));
            }
        }

        return alertas;
    }
}
