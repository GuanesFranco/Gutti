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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse registrar(@RequestBody PedidoDTO datos, Authentication usuarioLogueado) {
        return pedidoService.registrar(datos, usuarioLogueado.getName());
    }

    @GetMapping
    public List<PedidoResponse> consultar(
            @RequestParam(required = false) TipoDeEntrega tipoDeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pedidoService.consultar(tipoDeEntrega, desde, hasta);
    }

    @GetMapping("/recaudacion")
    public Double recaudacion(
            @RequestParam(required = false) TipoDeEntrega tipoDeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pedidoService.calcularRecaudacion(tipoDeEntrega, desde, hasta);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @RequestBody PedidoDTO datos) {
        return pedidoService.modificar(id, datos);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return pedidoService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
    }
}
