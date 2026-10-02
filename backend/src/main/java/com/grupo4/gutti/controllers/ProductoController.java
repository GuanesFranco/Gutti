package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;
import com.grupo4.gutti.dtos.Producto.ModifiedStateProductoResponse;
import com.grupo4.gutti.dtos.Producto.ProductoDTO;
import com.grupo4.gutti.dtos.Producto.ProductoResponse;
import com.grupo4.gutti.services.ProductoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;


    /**
 * @param id
 * @return
 */
@GetMapping("/{id}")
public ProductoResponse obtenerPorId(@PathVariable Long id) {
    return service.ObtenerPorId(id);
}

// getmapping si no tiene problemas devuelve 200, sino se puede hacer devolviendo un
// ResponseEntity<ProductoResponse> y en el return se pone ResponseEntity.codigoestado(Producto)

 /**
 * @param dto
 * @return
 */
@PostMapping("/crearProducto")
@ResponseStatus(HttpStatus.CREATED)
public ProductoResponse crearProducto(@Valid @RequestBody ProductoDTO dto){
    return service.CrearProducto(dto);

}

/**
 * @param id
 * @return
 */

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.OK)
public ModifiedStateProductoResponse eliminarProducto(@PathVariable Long id){
    return service.EliminarProducto(id);
}

/**
 * @param id
 * @return
 */

@PatchMapping ("/{id}")
@ResponseStatus (HttpStatus.OK)
public ModifiedStateProductoResponse activarProducto(@PathVariable Long id){
    return service.ActivarProducto(id);
}
    

/**
 * @param filtros
 * @return
 */
@GetMapping 
@ResponseStatus (HttpStatus.OK)
public List<ProductoResponse> filtroProductos(FilterProductoRequest request){
    return service.FiltroProductosDinamico(request);
}

/**
 * @param DTO
 * @return
 */

@PutMapping ("/modificarProducto/{id}")
@ResponseStatus (HttpStatus.OK)

public ProductoResponse actualizarProducto(@PathVariable Long id,@Valid ProductoDTO request){
    return service.ActualizarProducto(request, id);
}

}
