package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.cliente.ClienteDTO;
import com.grupo4.gutti.dtos.cliente.ClienteResponse;
import com.grupo4.gutti.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import static org.springframework.http.HttpStatus.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/mi-perfil")
    @ResponseStatus(OK)
    @Operation(summary = "Obtener el perfil del cliente actualmente autenticado")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil recuperado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Perfil de cliente no encontrado"))
        )
    })
    public ClienteResponse obtenerMiPerfil(Authentication authentication) {
        return clienteService.obtenerMiPerfil(authentication.getName());
    }

    @PutMapping("/mi-perfil")
    @ResponseStatus(OK)
    @Operation(summary = "Actualizar el perfil del cliente actualmente autenticado")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de los datos es incorrecto"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Perfil de cliente no encontrado"))
        )
    })
    public ClienteResponse actualizarMiPerfil(@Valid @RequestBody ClienteDTO dto, Authentication authentication) {
        return clienteService.actualizarMiPerfil(authentication.getName(), dto);
    }

    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "Listar todos los clientes (Solo Administradores)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de clientes recuperada exitosamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class))
            )
        )
    })
    public List<ClienteResponse> obtenerTodos() {
        return clienteService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(summary = "Obtener cliente por ID (Solo Administradores)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID obligatorio o inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El campo ID es obligatorio"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El cliente no existe"))
        )
    })
    public ClienteResponse obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(summary = "Actualizar cliente por ID (Solo Administradores)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente modificado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o incompletos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de los datos es incorrecto"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El cliente no existe"))
        )
    })
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return clienteService.actualizar(id, dto);
    }
}
