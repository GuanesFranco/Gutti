/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Datos de entrada para modificar los productos y la entrega de un pedido.
 */

package com.grupo4.gutti.dtos.pedido;

import com.grupo4.gutti.enums.TipoDeEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Datos para modificar un pedido. Reemplaza los datos de entrega y la lista completa de productos:
 * el stock de los productos anteriores se repone y se descuenta el de los nuevos.
 *
 * @author Alexis Monte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificarPedidoDTO {

    @NotNull(message = "El tipo de entrega es obligatorio (MOSTRADOR o DELIVERY)")
    private TipoDeEntrega tipoDeEntrega;

    private String nombreCliente;

    private String telefono;

    private String direccionEntrega;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<@Valid ItemPedidoDTO> items;
}
