package com.grupo4.gutti.repositories;

import com.grupo4.gutti.models.Producto;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategory(String category);

    boolean existsByNombre(String nombre);

    boolean existsById(Long id);

    boolean findByActiveState(boolean bool); // revisar
}
