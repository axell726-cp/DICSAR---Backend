package com.dicsar.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.dicsar.entity.Cliente;
import com.dicsar.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsByNumeroDocumento(cliente.getNumeroDocumento())) {
            throw new RuntimeException("El número de documento ya está registrado.");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado."));

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setApellidos(clienteActualizado.getApellidos());
        cliente.setRazonSocial(clienteActualizado.getRazonSocial());
        cliente.setDireccion(clienteActualizado.getDireccion());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setTipoDocumento(clienteActualizado.getTipoDocumento());
        cliente.setNumeroDocumento(clienteActualizado.getNumeroDocumento());
        cliente.setEsEmpresa(clienteActualizado.getEsEmpresa());
        cliente.setEstado(clienteActualizado.getEstado());
        cliente.setFechaActualizacion(java.time.LocalDateTime.now());

        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
