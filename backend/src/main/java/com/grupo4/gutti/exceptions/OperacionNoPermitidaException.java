/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Excepción para operaciones que violan una regla de negocio (HTTP 409).
 */

package com.grupo4.gutti.exceptions;

/**
 * Se lanza cuando una operación viola una regla de negocio, por ejemplo modificar un pedido ya entregado.
 *
 * @author Alexis Monte
 */
public class OperacionNoPermitidaException extends RuntimeException {

    /**
     * @param mensaje descripción de la regla que se incumplió
     */
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
