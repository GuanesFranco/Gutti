package com.grupo4.gutti.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.grupo4.gutti.enums.TipoDeEntrega;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ENTREGADO = "ENTREGADO";

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

    @Column(nullable = false)
    private String estado;

    private String nombreCliente;

    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeEntrega tipoDeEntrega;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // orphanRemoval: si se saca un item de la lista, también se borra de la base de datos.
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    public Double sumarItems() {
        double cantidad = 0;
        for (ItemPedido item : items) {
            cantidad = cantidad + item.getCantidad();
        }
        return cantidad;
    }

    public Double calcularCostoTotal() {
        double total = 0;
        for (ItemPedido item : items) {
            total = total + item.calcularSubtotal();
        }
        return total;
    }

    public boolean estaEntregado() {
        return ESTADO_ENTREGADO.equals(estado);
    }

}
