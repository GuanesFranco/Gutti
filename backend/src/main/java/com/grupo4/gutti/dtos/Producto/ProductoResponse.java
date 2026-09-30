package com.grupo4.gutti.dtos.Producto;



import lombok.Builder;

@Builder 
public class ProductoResponse {

    private String nombre;
    private String categoria;
    private double precio;
    private Integer stock;
    
}
