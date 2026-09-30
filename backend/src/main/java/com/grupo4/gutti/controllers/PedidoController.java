/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Controlador REST para la gestión de pedidos (registro de ventas).
 */

package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.pedido.PedidoRespuestaDTO;
import com.grupo4.gutti.dtos.pedido.RegistrarPedidoDTO;
import com.grupo4.gutti.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de pedidos. Solo accesibles para usuarios con rol ADMIN.
 *
 * @author Alexis Monte
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Registra una venta.
     *
     * @param dto           datos del pedido a registrar
     * @param autenticacion usuario autenticado (se toma su email del token JWT)
     * @return el pedido registrado (HTTP 201)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoRespuestaDTO registrar(@Valid @RequestBody RegistrarPedidoDTO dto, Authentication autenticacion) {
        log.info("request para registrar pedido por {}", autenticacion.getName());
        return pedidoService.registrar(dto, autenticacion.getName());
    }
}
