package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.services.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        log.info("request para registrar cliente: {}", dto.getEmail());
        autenticacionService.registrarCliente(dto);
        log.info("response registro de cliente completado exitosamente para {}", dto.getEmail());
    }

    @PostMapping("/iniciar-sesion")
    public AutenticacionRespuestaDTO iniciarSesion(@Valid @RequestBody IniciarSesionDTO dto) {
        log.info("request para iniciar sesion: {}", dto.getEmail());
        AutenticacionRespuestaDTO response = autenticacionService.iniciarSesion(dto);
        log.info("response inicio de sesion: token generado para {}", dto.getEmail());
        return response;
    }
}
