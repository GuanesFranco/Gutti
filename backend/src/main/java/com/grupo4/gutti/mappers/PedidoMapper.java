/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Conversión de entidades de pedido a DTOs de respuesta.
 */

package com.grupo4.gutti.mappers;

import com.grupo4.gutti.dtos.pedido.ItemPedidoRespuestaDTO;
import com.grupo4.gutti.dtos.pedido.PedidoRespuestaDTO;
import com.grupo4.gutti.models.ItemPedido;
import com.grupo4.gutti.models.Pedido;

/**
 * Convierte entidades de pedido a DTOs de respuesta para no exponer las entidades JPA en la API.
 *
 * @author Alexis Monte
 */
public final class PedidoMapper {

    private PedidoMapper() {
    }

    /**
     * @param pedido entidad a convertir
     * @return DTO con los datos del pedido, sus ítems y el total
     */
    public static PedidoRespuestaDTO aRespuesta(Pedido pedido) {
        return PedidoRespuestaDTO.builder()
                .id(pedido.getId())
                .fechaHora(pedido.getFechaHora())
                .estado(pedido.getEstado())
                .tipoDeEntrega(pedido.getTipoDeEntrega())
                .nombreCliente(pedido.getNombreCliente())
                .telefono(pedido.getTelefono())
                .direccionEntrega(pedido.getDireccionEntrega())
                .items(pedido.getItems().stream().map(PedidoMapper::aItemRespuesta).toList())
                .total(pedido.calcularCostoTotal())
                .build();
    }

    /**
     * @param item ítem a convertir
     * @return DTO con el producto, la cantidad, el precio unitario y el subtotal
     */
    public static ItemPedidoRespuestaDTO aItemRespuesta(ItemPedido item) {
        return ItemPedidoRespuestaDTO.builder()
                .productoId(item.getProducto().getId())
                .nombreProducto(item.getProducto().getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.calcularSubtotal())
                .build();
    }
}
