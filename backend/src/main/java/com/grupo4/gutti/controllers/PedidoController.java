package com.grupo4.gutti.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grupo4.gutti.dtos.pedido.PedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoResponse;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.services.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un nuevo pedido")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Pedido registrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de los datos es incorrecto."))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto al registrar el pedido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se puede registrar el pedido."))
        )
    })
    public PedidoResponse registrar(@RequestBody PedidoDTO datos, Authentication usuarioLogueado) {
        return pedidoService.registrar(datos, usuarioLogueado.getName());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Consultar pedidos con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Pedidos recuperados exitosamente",
            content = @Content(
                mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de filtrado inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Los parámetros de fecha u orden son inválidos."))
        )
    })
    public List<PedidoResponse> consultar(
            @RequestParam(required = false) TipoDeEntrega tipoDeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pedidoService.consultar(tipoDeEntrega, desde, hasta);
    }

    @GetMapping("/recaudacion")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Calcular recaudación total con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Recaudación calculada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "number", example = "15250.50"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de fecha inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de las fechas es incorrecto."))
        )
    })
    public Double recaudacion(
            @RequestParam(required = false) TipoDeEntrega tipoDeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pedidoService.calcularRecaudacion(tipoDeEntrega, desde, hasta);
    }

   @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener un pedido por su ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Pedido encontrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID obligatorio o inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El campo id es obligatorio"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Pedido no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El pedido no existe"))
        )
    })
    public PedidoResponse obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Modificar un pedido existente")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Pedido modificado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Pedido no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El pedido no existe"))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto al modificar el pedido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se puede modificar un pedido finalizado"))
        )
    })
    public PedidoResponse modificar(@PathVariable Long id, @RequestBody PedidoDTO datos) {
        return pedidoService.modificar(id, datos);
    }

   @PatchMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cambiar el estado de un pedido")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estado cambiado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Estado u ID inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El estado proporcionado no es válido"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Pedido no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El pedido no existe"))
        )
    })
    public PedidoResponse cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return pedidoService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
        @ApiResponse(
            responseCode = "400", 
            description = "ID obligatorio",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El campo id es obligatorio"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Pedido no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El pedido no existe"))
        )
    })
    public void eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
    }
}
