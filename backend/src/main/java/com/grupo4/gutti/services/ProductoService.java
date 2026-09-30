package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.Producto.*;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional  // se ejecuta en una sola transacción, si todo sale bien hace el commit a la bd, si hay excepcion no 
                // controlada ejecuta un rollback, se puede poner (readOnly=true) para optimizar procesos de lectura y despues
                // en cada metodo que requeiera escritura se vuelve a poner la etiqueta


public class ProductoService {

    private final ProductoRepository ProductoRepository;

    public ProductoResponse CrearProducto(ProductoDTO request){

        if(ProductoRepository.existsByNombre(request.getNombre())){
            log.warn("El producto {} ya existe", request.getNombre());
            throw new IllegalArgumentException("No se puede crear un producto ya existente");
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

    public ProductoResponse ObtenerPorNombre(String nombre){

         if(nombre==null || nombre.isBlank()){
            log.warn("El nombre es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo nombre es obligatorio");
        }
        
        Producto ProductoSolicitado = ProductoRepository.findByNombre(nombre);

            if (ProductoSolicitado==null) {
            log.warn("El producto consultado no existe");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El producto no existe");
            }
            
        return ProductoResponse.builder()
        .nombre(ProductoSolicitado.getNombre())
        .categoria(ProductoSolicitado.getCategoria())
        .precio(ProductoSolicitado.getPrecio())
        .stock(ProductoSolicitado.getStock())
        .fechaCreacion(ProductoSolicitado.getFechaCreacion())
        .fechaModificacion(ProductoSolicitado.getFechaModificacion())
        .build();
    }

    public ModifiedStateProductoResponse EliminarProducto(String nombre){
        
     Producto ProductoSolicitado = ProductoRepository.findByNombre(nombre);

       if(nombre==null || nombre.isBlank()){
            log.warn("El nombre es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo nombre es obligatorio");
        }

            if (ProductoSolicitado==null) {
                log.warn("El producto consultado no existe");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El producto no existe");
            }

            if(!ProductoSolicitado.isEstadoActivo()){
                log.warn("El producto ya esta inactivo");
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El producto ya se encuentra desactivado");
            }

            ProductoSolicitado.setEstadoActivo(false);

      return ModifiedStateProductoResponse.builder()
      .fechaModificacion(ProductoSolicitado.getFechaModificacion())
      .estadoActivo(ProductoSolicitado.isEstadoActivo())
      .build();  
    }

    public ModifiedStateProductoResponse ActivarProducto (String nombre){

        // si bien es practicamente igual al delete, lo ideal es tener separada la logica para dar de alta y para eliminar
        if(nombre==null || nombre.isBlank()){
            log.warn("El nombre es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo nombre es obligatorio");
        }

         Producto ProductoSolicitado = ProductoRepository.findByNombre(nombre);

            if (ProductoSolicitado==null) {
                log.warn("El producto consultado no existe");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El producto no existe");
            }

            if(ProductoSolicitado.isEstadoActivo()){
                log.warn("El producto ya esta inactivo");
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El producto ya esta activo");
            }

            ProductoSolicitado.setEstadoActivo(true);

      return ModifiedStateProductoResponse.builder()
      .fechaModificacion(ProductoSolicitado.getFechaModificacion())
      .estadoActivo(ProductoSolicitado.isEstadoActivo())
      .build();  
    }

    public List<ProductoResponse> FiltroProductosDinamico (FilterProductoRequest request){

       List<Producto> query = ProductoRepository.filterQuery(request);

       List<ProductoResponse> lista = query.stream()
            .map(p -> ProductoResponse.builder()
                    .nombre(p.getNombre())
                    .precio(p.getPrecio())
                    .categoria(p.getCategoria())
                    .stock(p.getStock())
                    .fechaCreacion(p.getFechaCreacion())
                    .fechaModificacion(p.getFechaModificacion())
                    .build()
            )
            .toList();

    return lista;
    }

    


}
