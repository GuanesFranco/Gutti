package com.grupo4.gutti.dtos.pedido;

import java.util.List;

import com.grupo4.gutti.enums.TipoDeEntrega;

import lombok.Data;

@Data
public class PedidoDTO {

    private TipoDeEntrega tipoDeEntrega;
    private String nombreCliente;
    private String telefono;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productoId;
        private Integer cantidad;
    }
}
