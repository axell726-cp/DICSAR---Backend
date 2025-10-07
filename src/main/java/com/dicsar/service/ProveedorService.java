package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dicsar.entity.Proveedor;
import com.dicsar.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> obtener(Long id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor guardar(Proveedor proveedor) {
        validarProveedor(proveedor);

        if (proveedor.getIdProveedor() == null) {
            proveedor.setFechaCreacion(LocalDateTime.now());
            proveedor.setEstado(true);
        } else {
            proveedor.setFechaActualizacion(LocalDateTime.now());
        }

        return proveedorRepository.save(proveedor);
    }

    public void eliminar(Long id) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        if (proveedorOpt.isEmpty()) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        Proveedor proveedor = proveedorOpt.get();
        if (Boolean.TRUE.equals(proveedor.getEstado())) {
            throw new RuntimeException("No se puede eliminar un proveedor activo. Desactívelo primero.");
        }

        proveedorRepository.deleteById(id);
    }

    private void validarProveedor(Proveedor proveedor) {
        if (!StringUtils.hasText(proveedor.getRuc())) {
            throw new RuntimeException("El RUC del proveedor es obligatorio.");
        }

        if (proveedorRepository.existsByRuc(proveedor.getRuc()) &&
            (proveedor.getIdProveedor() == null ||
             !proveedorRepository.findByRuc(proveedor.getRuc()).get().getIdProveedor().equals(proveedor.getIdProveedor()))) {
            throw new RuntimeException("El RUC ya está registrado para otro proveedor.");
        }

        if (!StringUtils.hasText(proveedor.getRazonSocial())) {
            throw new RuntimeException("La razón social es obligatoria.");
        }

        if (proveedor.getEmail() != null && proveedorRepository.existsByEmail(proveedor.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado para otro proveedor.");
        }
    }
}
