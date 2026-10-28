package com.grupo4.gutti.dtos.Producto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data 
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String categoria;
    private double precio;
    private Integer stock;
    private String descripcion;
    private String imagenUrl;
    private boolean estadoActivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
