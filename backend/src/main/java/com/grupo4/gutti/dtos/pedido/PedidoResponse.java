package com.grupo4.gutti.dtos.pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.grupo4.gutti.enums.TipoDeEntrega;

import lombok.Data;

@Data
public class PedidoResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private String estado;
    private TipoDeEntrega tipoDeEntrega;
    private String nombreCliente;
    private String telefono;
    private List<Item> items = new ArrayList<>();
    private Double total;

    @Data
    public static class Item {
        private String nombreProducto;
        private Integer cantidad;
        private Double subtotal;
    }
}
