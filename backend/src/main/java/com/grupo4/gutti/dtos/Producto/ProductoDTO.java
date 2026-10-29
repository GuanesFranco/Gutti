package com.grupo4.gutti.dtos.Producto;

import com.grupo4.gutti.enums.CategoriasEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La categoria es obligatoria")
    private CategoriasEnum categoria;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    private String descripcion;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    private String imagenUrl;
}
