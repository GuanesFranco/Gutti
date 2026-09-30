package com.grupo4.gutti.dtos.Producto;

import com.grupo4.gutti.enums.CategoriasEnum;

import lombok.Data;

@Data
public class FilterProductoRequest {

    private String nombre;
    private CategoriasEnum categoria;
    private String ordenStock; // ascendente o descendente

}
