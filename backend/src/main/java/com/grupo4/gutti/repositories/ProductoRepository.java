package com.grupo4.gutti.repositories;

import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;

@Repository

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto>{

    List<Producto> findByCategory(String categoria);

    boolean existsByNombre(String nombre);

    boolean existsById(Long id);

    Producto findByNombre(String nombre);

    boolean findByActiveState(boolean estadoActivo); // revisar

    
    default List<Producto> filterQuery(FilterProductoRequest request){

    // lo condicionamos a que solo busque productos activos
    Specification<Producto> spec = (root, query, cb) -> 
        cb.equal(root.get("estadoActivo"), true);

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("nombre")), "%" + request.getNombre().toLowerCase().trim() + "%")
            );
        }

        if (request.getCategoria() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("categoria"), request.getCategoria().name())
            );
        }

    Sort sort = Sort.unsorted();

        if (request.getOrdenStock() != null && !request.getOrdenStock().isBlank()) {
        if ("ASC".equalsIgnoreCase(request.getOrdenStock().trim())) {
            sort = Sort.by(Sort.Direction.ASC, "stock");
        } else if ("DESC".equalsIgnoreCase(request.getOrdenStock().trim())) {
            sort = Sort.by(Sort.Direction.DESC, "stock");
        }
    }
    
        return findAll(spec,sort);
    }

}

