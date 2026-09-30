/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Excepción para recursos inexistentes (se responde con HTTP 404).
 */

package com.grupo4.gutti.exceptions;

/** Se lanza cuando se busca un recurso (pedido, producto, etc.) que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
