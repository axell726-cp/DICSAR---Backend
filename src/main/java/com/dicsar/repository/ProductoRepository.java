package com.dicsar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dicsar.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

}
