package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.services.AutenticacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/registro")
    @ResponseStatus(CREATED)
    @Operation(summary = "Registrar un nuevo cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente"),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de registro inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de los datos es incorrecto o faltan campos obligatorios."))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "El correo electrónico ya se encuentra registrado",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El email ya está en uso."))
        )
    })
    public void registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        log.info("request para registrar cliente: {}", dto.getEmail());
        autenticacionService.registrarCliente(dto);
        log.info("response registro de cliente completado exitosamente para {}", dto.getEmail());
    }

    @PostMapping("/iniciar-sesion")
    @ResponseStatus(OK)
    @Operation(summary = "Iniciar sesión para obtener un token de acceso")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Sesión iniciada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutenticacionRespuestaDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Credenciales con formato inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El formato de las credenciales es incorrecto."))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciales incorrectas",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Credenciales inválidas."))
        )
    })
    public AutenticacionRespuestaDTO iniciarSesion(@Valid @RequestBody IniciarSesionDTO dto) {
        log.info("request para iniciar sesion: {}", dto.getEmail());
        AutenticacionRespuestaDTO response = autenticacionService.iniciarSesion(dto);
        log.info("response inicio de sesion: token generado para {}", dto.getEmail());
        return response;
    }
}
