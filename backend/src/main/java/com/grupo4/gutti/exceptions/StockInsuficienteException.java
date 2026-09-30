/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Excepción para ventas que superan el stock disponible (HTTP 409).
 */

package com.grupo4.gutti.exceptions;

/** Se lanza cuando se intenta vender más unidades de las que hay en stock. */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
