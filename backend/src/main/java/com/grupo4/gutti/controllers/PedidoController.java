/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Controlador REST para la gestión de pedidos (registro y consulta de ventas).
 */

package com.grupo4.gutti.controllers;

import com.grupo4.gutti.dtos.pedido.HistorialPedidosDTO;
import com.grupo4.gutti.dtos.pedido.PedidoRespuestaDTO;
import com.grupo4.gutti.dtos.pedido.RegistrarPedidoDTO;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    /**
     * Historial de ventas. Ejemplo: GET /api/v1/pedidos?tipoDeEntrega=DELIVERY (todos los filtros son opcionales)
     *
     * @param tipoDeEntrega filtro opcional por tipo de entrega
     * @param desde         filtro opcional de fecha inicial (AAAA-MM-DD)
     * @param hasta         filtro opcional de fecha final (AAAA-MM-DD)
     * @return pedidos encontrados y recaudación total
     */
    @GetMapping
    public HistorialPedidosDTO consultar(
            @RequestParam(required = false) TipoDeEntrega tipoDeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        log.info("request para consultar pedidos: tipo={}, desde={}, hasta={}", tipoDeEntrega, desde, hasta);
        return pedidoService.consultar(tipoDeEntrega, desde, hasta);
    }

    /**
     * @param id identificador del pedido
     * @return el detalle del pedido
     */
    @GetMapping("/{id}")
    public PedidoRespuestaDTO obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id);
    }
}
