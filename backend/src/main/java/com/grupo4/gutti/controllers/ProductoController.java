package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;
import com.grupo4.gutti.dtos.Producto.ModifiedStateProductoResponse;
import com.grupo4.gutti.dtos.Producto.ProductoDTO;
import com.grupo4.gutti.dtos.Producto.ProductoResponse;
import com.grupo4.gutti.services.ProductoService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
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
 * 
 */
@GetMapping("obtenerPorId/{id}")
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "Productos encontrados exitosamente.", 
        content = @Content(schema = @Schema(implementation = ProductoResponse.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "El parametro proporcionado es invalido.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "string", 
            example = "El formato de los parámetros de búsqueda es incorrecto.") 
        )
    ),
    @ApiResponse(
        responseCode = "404", 
        description = "No se encontraron productos.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type="string",
            example = "No se encontraron registros con los filtros proporcionados.") 
        )
    )
})
public ProductoResponse obtenerPorId(@PathVariable Long id) {
    return service.obtenerPorId(id);
}

// getmapping si no tiene problemas devuelve 200, sino se puede hacer devolviendo un
// ResponseEntity<ProductoResponse> y en el return se pone ResponseEntity.codigoestado(Producto)

 /**
 * @param dto
 * 
 */
@PostMapping("/crearProducto")
@ResponseStatus(HttpStatus.CREATED)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "201", 
        description = "Producto creado exitosamente.", 
        content = @Content(schema = @Schema(implementation = ProductoResponse.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "Alguno de los parametros son invalidos.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "string", 
            example = "El formato de uno/s parametros son incorrectos.") 
        )
    ),
    @ApiResponse(
        responseCode = "409", 
        description = "Ya existe un producto con algunos de los parametros.",
        content = @Content(
            mediaType = "application/json",
            schema =  @Schema(type="string",
            example = "Error, ya existe un producto con ese nombre.") 
        )
    )
})
public ProductoResponse crearProducto(@Valid @RequestBody ProductoDTO dto){
    return service.crearProducto(dto);

}

/**
 * @param id
 * 
 */

@DeleteMapping("/eliminarProducto/{id}")


@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "Producto eliminado exitosamente.", 
        content = @Content(schema = @Schema(implementation = ModifiedStateProductoResponse.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "El parametro proporcionado es invalido.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "string",
            example = "El formato del id proporcionado no es valido.")
        )
    ),

    @ApiResponse(
        responseCode = "404", 
        description = "No se encontró el producto a eliminar.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string", 
            example = "No se encontro el producto a eliminar.")
        )
    ),
    @ApiResponse(
        responseCode = "409", 
        description = "El producto ya se encuentra inactivo.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string",
            example = "El producto ya se encuentra inactivo.") 
        )
    )
})
public ModifiedStateProductoResponse eliminarProducto(@PathVariable Long id){
    return service.eliminarProducto(id);
}

/**
 * @param id
 * 
 */

@PatchMapping ("/activarProducto/{id}")

@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "Producto activado exitosamente..", 
        content = @Content(schema = @Schema(implementation = ModifiedStateProductoResponse.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "El parametro proporcionado es invalido.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string",
            example =  "El formato del id proporcionado no es valido.") 
        )
    ),
    @ApiResponse(
        responseCode = "404", 
        description = "No se encontró el producto a activar.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "string",
            example = "No se encontro el producto a activar.") 
        )
    ),
    @ApiResponse(
        responseCode = "409", 
        description = "El producto ya se encuentra activo.",
        content = @Content(
        mediaType = "application/json",
        schema = @Schema(type = "string",
        example = "El producto ya se encuentra activo.") 
        )
    )
})

public ModifiedStateProductoResponse activarProducto(@PathVariable Long id){
    return service.activarProducto(id);
}
    

/**
 * @param filtros
 * 
 */
@GetMapping("/consultarproductos") 
@ApiResponse(
    responseCode = "200", 
    description = "Productos recuperados exitosamente.", 
    content = @Content(
        mediaType = "application/json",
        array = @ArraySchema(
        schema = @Schema(implementation = ProductoResponse.class))
    )
)
public List<ProductoResponse> filtroProductos(@ParameterObject FilterProductoRequest request){
    return service.filtroProductosDinamico(request);
}

/**
 * @param DTO
 * 
 */

@PutMapping ("/modificarProducto/{id}")
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "Producto modificado exitosamente.", 
        content = @Content(schema = @Schema(implementation = ProductoResponse.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "Alguno de los parametros son invalidos.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string",
            example = "El formato de uno/s parametros son incorrectos.") 
        )
    ),
    @ApiResponse(
        responseCode = "404", 
        description = "No se encontró el producto a modificar.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string", 
            example = "No se encontró el producto a modificar.")
        )
    ),
    @ApiResponse(
        responseCode = "409", 
        description = "Algunos de los parametros no son admitidos o el objeto esta inactivo.",
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(type = "string",
            example = "No se puede modificar un producto inactivo o reemplazar el nombre por otro existente.") 
        )
    )
})
public ProductoResponse actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO request){
    return service.actualizarProducto(request, id);
}

}
