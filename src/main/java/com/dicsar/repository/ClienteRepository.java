package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.dicsar.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    boolean existsByNumeroDocumento(String numeroDocumento);
}
