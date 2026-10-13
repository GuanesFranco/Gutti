package com.grupo4.gutti.dtos.Producto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder 
@Data

public class ModifiedStateProductoResponse {

    private LocalDateTime fechaModificacion;
    private boolean estadoActivo;


}
