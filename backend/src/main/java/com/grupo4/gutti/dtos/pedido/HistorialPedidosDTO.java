/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Respuesta del historial de ventas con la recaudación total.
 */

package com.grupo4.gutti.dtos.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta del historial de ventas: listado de pedidos y recaudación total del período consultado.
 *
 * @author Alexis Monte
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPedidosDTO {
    private List<PedidoRespuestaDTO> pedidos;
    private Double recaudacionTotal;
    /** Mensaje informativo; se completa cuando no hay resultados. */
    private String mensaje;
}
