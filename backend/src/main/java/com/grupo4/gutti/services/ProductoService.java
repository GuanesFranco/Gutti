package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.Producto.*;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j

@Transactional  // se ejecuta en una sola transacción, si todo sale bien hace el commit a la bd, si hay excepcion no 
                // controlada ejecuta un rollback
public class ProductoService {

    private final ProductoRepository ProductoRepository;

    public ProductoResponse CrearProducto(ProductoDTO request){

        if(ProductoRepository.existsByNombre(request.getNombre())){
            log.warn("El producto {} ya existe", request.getNombre());
            throw new IllegalArgumentException("Este producto ya existe");
        }

        Producto producto = Producto.builder()
        .nombre(request.getNombre())
        .descripcion(request.getDescripcion())
        .categoria(request.getCategoria())
        .precio(request.getPrecio())
        .stock(request.getStock())
        .estadoActivo(true) 
        .build();

        Producto ProductoGuardado = ProductoRepository.save(producto); // lo guardamos en una variable para en el response poder
                                                                      // conseguir info que se genera automaticamente ej id

        
         return ProductoResponse.builder()
        .nombre(ProductoGuardado.getNombre())
        .categoria(ProductoGuardado.getCategoria())
        .precio(ProductoGuardado.getPrecio())
        .stock(ProductoGuardado.getStock())
        .build();    
    }


    


}
