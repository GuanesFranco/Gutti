package com.grupo4.gutti.dtos.Producto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data 
public class ProductoResponse {

    private String nombre;
    private String categoria;
    private double precio;
    private Integer stock;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

}
