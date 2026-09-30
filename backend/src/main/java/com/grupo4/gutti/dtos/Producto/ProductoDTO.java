package com.grupo4.gutti.dtos.Producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data

public class ProductoDTO {

    // los getters y setters se usan con @data, se crean en tiempo de ejecución

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoria es obligatoria")
    private String categoria;

    @NotBlank(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private double precio;

    @NotBlank(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a 0")
    private Integer stock;

    private String descripcion;
}
