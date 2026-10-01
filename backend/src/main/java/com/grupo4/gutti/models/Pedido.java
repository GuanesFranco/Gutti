package com.grupo4.gutti.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.grupo4.gutti.enums.EstadoPedido;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.exceptions.OperacionNoPermitidaException;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    public static final String MENSAJE_PEDIDO_ENTREGADO = "No se puede eliminar un pedido ya entregado.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    private String nombreCliente;

    private String telefono;

    /** Obligatoria solo para pedidos con entrega a domicilio (DELIVERY). */
    private String direccionEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeEntrega tipoDeEntrega;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    /**
     * Agrega un producto al pedido descontando su stock.
     *
     * @param producto producto vendido
     * @param cantidad unidades vendidas (mayor a cero)
     * @throws com.grupo4.gutti.exceptions.StockInsuficienteException si no hay stock suficiente;
     *         en ese caso no se agrega nada
     */
    public void agregarItem(Producto producto, int cantidad) {
        producto.descontarStock(cantidad);
        ItemPedido item = ItemPedido.builder()
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(producto.getPrecio())
                .pedido(this)
                .build();
        items.add(item);
    }

    /**
     * Devuelve al stock todos los productos del pedido y vacía la lista de ítems.
     */
    public void quitarItemsReponiendoStock() {
        items.forEach(item -> item.getProducto().reponerStock(item.getCantidad()));
        items.clear();
    }

    /**
     * @return cantidad total de unidades del pedido
     */
    public int sumarItems() {
        return items.stream().mapToInt(ItemPedido::getCantidad).sum();
    }

    /**
     * @return total a pagar: suma de los subtotales de los ítems
     */
    public Double calcularCostoTotal() {
        return items.stream().mapToDouble(ItemPedido::calcularSubtotal).sum();
    }

    /**
     * @return true si el pedido ya fue entregado
     */
    public boolean estaEntregado() {
        return estado == EstadoPedido.ENTREGADO;
    }

    /**
     * @throws OperacionNoPermitidaException si el pedido ya fue entregado
     */
    public void validarQueSePuedeEliminar() {
        if (estaEntregado()) {
            throw new OperacionNoPermitidaException(MENSAJE_PEDIDO_ENTREGADO);
        }
    }

    /**
     * @throws OperacionNoPermitidaException si el pedido ya fue entregado
     */
    public void validarQueSePuedeModificar() {
        if (estaEntregado()) {
            throw new OperacionNoPermitidaException("No se puede modificar un pedido ya entregado.");
        }
    }

}
