package com.grupo4.gutti.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutenticacionRespuestaDTO {
    private String token;
    private String rol;
    private String email;

    public AutenticacionRespuestaDTO(String token) {
        this.token = token;
    }
}
