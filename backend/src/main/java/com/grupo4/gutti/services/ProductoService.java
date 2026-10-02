package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.Producto.*;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.apache.catalina.connector.Response;
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


    private ProductoResponse mapearAProductoResponse(Producto producto) {
        return ProductoResponse.builder()
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .descripcion(producto.getDescripcion())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaModificacion(producto.getFechaModificacion())
                .build();
    }


    public ProductoResponse CrearProducto(ProductoDTO request){

        if(ProductoRepository.existsByNombre(request.getNombre().trim())){
            log.warn("El producto {} ya existe", request.getNombre());
            throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede crear un producto ya existente");
        }

    
        Producto producto = Producto.builder()
        .nombre(request.getNombre())
        .descripcion(request.getDescripcion())
        .categoria(request.getCategoria().name())
        .precio(request.getPrecio())
        .stock(request.getStock())
        .estadoActivo(true) 
        .build();

        Producto ProductoGuardado = ProductoRepository.save(producto); // lo guardamos en una variable para en el response poder
                                                                      // conseguir info que se genera automaticamente ej id

        
        return mapearAProductoResponse(ProductoGuardado);
    }

    public ProductoResponse ObtenerPorId(Long id){

         if(id==null){
            log.warn("El id es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo id es obligatorio");
        }
        // el find by id del repository devuelve un optional <t> si no encuentra nada devuelve un .empty y lanza la excepcion
        // se realizo el cambio en las funcionalidades por cambio de nombre 
        Producto ProductoSolicitado = ProductoRepository.findById(id).orElseThrow(() -> {
            log.warn("El producto consultado no existe");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe");
        });

        return mapearAProductoResponse(ProductoSolicitado);
    }

    public ModifiedStateProductoResponse EliminarProducto(Long Id){
        
       if(Id == null){
            log.warn("El id es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo id es obligatorio");
        }

        Producto ProductoSolicitado = ProductoRepository.findById(Id).orElseThrow(() -> {
            log.warn("El producto consultado no existe");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe");
        });

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

    public ModifiedStateProductoResponse ActivarProducto (Long id){

        // si bien es practicamente igual al delete, lo ideal es tener separada la logica para dar de alta y para eliminar
        if(id == null){
            log.warn("El id es obligatorio");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El campo id es obligatorio");
        }

         Producto ProductoSolicitado = ProductoRepository.findById(id).orElseThrow(() -> {
            log.warn("El producto consultado no existe");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe");
        });

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

       return query.stream()
                .map(this::mapearAProductoResponse)
                .toList();
    }

    public ProductoResponse ActualizarProducto (ProductoDTO request, Long id){
        
        Producto Actualizar = ProductoRepository.findById(id).orElseThrow(() -> {
            log.warn("El producto consultado no existe");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe");
        });
    
        if (!request.getNombre().trim().equalsIgnoreCase(Actualizar.getNombre())) {
        if (ProductoRepository.existsByNombre(request.getNombre().trim())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Ya existe otro producto con el nombre: " + request.getNombre()
            );
        }
    }

        if(!Actualizar.isEstadoActivo()){
            log.warn("No se puede actualizar un plato inactivo");
            throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede actualizar un producto inactivo");
        }

        Actualizar.setNombre(request.getNombre().trim());
        Actualizar.setCategoria(request.getCategoria().name());
        Actualizar.setPrecio(request.getPrecio());
        Actualizar.setStock(request.getStock());
        Actualizar.setDescripcion(request.getDescripcion());

        Producto ProductoGuardado = ProductoRepository.save(Actualizar);

        return mapearAProductoResponse(ProductoGuardado);
    }

}
