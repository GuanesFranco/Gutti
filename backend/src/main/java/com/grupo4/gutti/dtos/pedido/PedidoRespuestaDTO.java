/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Datos de salida de un pedido con sus ítems y el total a pagar.
 */

package com.grupo4.gutti.dtos.pedido;

import com.grupo4.gutti.enums.EstadoPedido;
import com.grupo4.gutti.enums.TipoDeEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRespuestaDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private EstadoPedido estado;
    private TipoDeEntrega tipoDeEntrega;
    private String nombreCliente;
    private String telefono;
    private String direccionEntrega;
    private List<ItemPedidoRespuestaDTO> items;
    private Double total;
}
