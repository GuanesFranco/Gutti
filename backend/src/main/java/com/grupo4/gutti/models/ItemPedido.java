package com.grupo4.gutti.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "items_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio del producto al momento de la venta. Se guarda para que el historial
     * no cambie si luego se modifica el precio del producto.
     */
    @Column(nullable = false)
    private Double precioUnitario;

    // Se excluye de toString/equals/hashCode para evitar recursión infinita con Pedido.items
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * @return precio unitario al momento de la venta multiplicado por la cantidad
     */
    public Double calcularSubtotal() {
        return precioUnitario * cantidad;
    }

}
