package com.grupo4.gutti.dtos.Producto;

import com.grupo4.gutti.enums.CategoriasEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data

public class ProductoDTO {

    // los getters y setters se usan con @data, se crean en tiempo de ejecución

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La categoria es obligatoria")
    private CategoriasEnum categoria;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a 0")
    private Integer stock;

    private String descripcion;
}
