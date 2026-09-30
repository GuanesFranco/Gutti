package com.grupo4.gutti.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.grupo4.gutti.exceptions.StockInsuficienteException;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private boolean estadoActivo;

    /**
     * Descuenta unidades del stock al venderse el producto.
     *
     * @param cantidad unidades a descontar (mayor a cero)
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    public void descontarStock(int cantidad) {
        validarCantidadPositiva(cantidad);
        if (!tieneStockSuficiente(cantidad)) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + nombre + "'. Disponible: " + stock
                            + ", solicitado: " + cantidad + ".");
        }
        this.stock -= cantidad;
    }


    /**
     * @param cantidad unidades requeridas
     * @return true si el stock alcanza para esa cantidad
     */
    public boolean tieneStockSuficiente(int cantidad) {
        return stock != null && stock >= cantidad;
    }

    private static void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }

}
