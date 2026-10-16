package com.grupo4.gutti.dtos.cliente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {

    private Long id;
    private UUID usuarioId;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
