/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Datos de entrada para cambiar el estado de un pedido.
 */

package com.grupo4.gutti.dtos.pedido;

import com.grupo4.gutti.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nuevo estado de un pedido (PENDIENTE o ENTREGADO).
 *
 * @author Alexis Monte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoPedidoDTO {

    @NotNull(message = "El estado es obligatorio (PENDIENTE o ENTREGADO)")
    private EstadoPedido estado;
}
