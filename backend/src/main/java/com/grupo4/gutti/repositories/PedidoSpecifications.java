/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Filtros combinables para consultar el historial de pedidos.
 */

package com.grupo4.gutti.repositories;

import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.models.Pedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Filtros opcionales y combinables para consultar el historial de pedidos.
 *
 * @author Alexis Monte
 */
public final class PedidoSpecifications {

    private PedidoSpecifications() {
    }

    /**
     * @param tipoDeEntrega MOSTRADOR o DELIVERY
     * @return filtro por tipo de entrega
     */
    public static Specification<Pedido> conTipoDeEntrega(TipoDeEntrega tipoDeEntrega) {
        return (root, query, cb) -> cb.equal(root.get("tipoDeEntrega"), tipoDeEntrega);
    }

    /**
     * @param desde fecha y hora mínima (inclusive)
     * @return filtro de pedidos realizados desde esa fecha
     */
    public static Specification<Pedido> desde(LocalDateTime desde) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaHora"), desde);
    }

    /**
     * Límite superior exclusivo: se usa el inicio del día siguiente para incluir todo el día "hasta".
     *
     * @param limite fecha y hora máxima (exclusiva)
     * @return filtro de pedidos anteriores a ese momento
     */
    public static Specification<Pedido> antesDe(LocalDateTime limite) {
        return (root, query, cb) -> cb.lessThan(root.get("fechaHora"), limite);
    }
}
