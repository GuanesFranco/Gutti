package com.grupo4.gutti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.dtos.pedido.PedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoResponse;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.services.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de PedidoController")
class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
        auth = new UsernamePasswordAuthenticationToken("cliente@gutti.com", null, Collections.emptyList());
    }

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private PedidoDTO pedidoDTO(TipoDeEntrega tipo, String nombreCliente, String telefono) {
        PedidoDTO dto = new PedidoDTO();
        dto.setTipoDeEntrega(tipo);
        dto.setNombreCliente(nombreCliente);
        dto.setTelefono(telefono);
        dto.setItems(new ArrayList<>());
        return dto;
    }

    private PedidoResponse pedidoResponse(Long id, String estado, Double total, TipoDeEntrega tipo) {
        PedidoResponse resp = new PedidoResponse();
        resp.setId(id);
        resp.setEstado(estado);
        resp.setTotal(total);
        resp.setTipoDeEntrega(tipo);
        resp.setFechaHora(LocalDateTime.now());
        resp.setItems(new ArrayList<>());
        return resp;
    }

    // ==========================================
    // POST /api/v1/pedidos
    // ==========================================

    @Test
    @DisplayName("POST / - Debe retornar 201 Created al registrar un pedido con éxito")
    void registrar_conDatosValidos_retornaCreatedYPedido() throws Exception {
        // Arrange
        PedidoDTO dto = pedidoDTO(TipoDeEntrega.MOSTRADOR, "Ana", "11223344");
        PedidoResponse responseMock = pedidoResponse(1L, "PENDIENTE", 5000.0, TipoDeEntrega.MOSTRADOR);

        when(pedidoService.registrar(any(PedidoDTO.class), eq("cliente@gutti.com"))).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(5000.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    // ==========================================
    // GET /api/v1/pedidos
    // ==========================================

    @Test
    @DisplayName("GET / - Debe retornar 200 OK con la lista de pedidos")
    void consultar_conFiltros_retornaOkYLista() throws Exception {
        // Arrange
        PedidoResponse p1 = pedidoResponse(1L, "PENDIENTE", 3000.0, TipoDeEntrega.MOSTRADOR);
        when(pedidoService.consultar(any(), any(), any())).thenReturn(List.of(p1));

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].total").value(3000.0));
    }

    // ==========================================
    // GET /api/v1/pedidos/recaudacion
    // ==========================================

    @Test
    @DisplayName("GET /recaudacion - Debe retornar 200 OK con el monto recaudado")
    void recaudacion_conParametrosValidos_retornaOkYMonto() throws Exception {
        // Arrange
        when(pedidoService.calcularRecaudacion(any(), any(), any())).thenReturn(7500.0);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/recaudacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(7500.0));
    }

    // ==========================================
    // GET /api/v1/pedidos/{id}
    // ==========================================

    @Test
    @DisplayName("GET /{id} - Debe retornar 200 OK y el pedido cuando el ID existe")
    void obtenerPorId_conIdExistente_retornaOkYPedido() throws Exception {
        // Arrange
        PedidoResponse responseMock = pedidoResponse(1L, "PENDIENTE", 4500.0, TipoDeEntrega.DELIVERY);
        when(pedidoService.obtenerPorId(1L)).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoDeEntrega").value("DELIVERY"));
    }

    // ==========================================
    // PUT /api/v1/pedidos/{id}
    // ==========================================

    @Test
    @DisplayName("PUT /{id} - Debe retornar 200 OK al modificar un pedido")
    void modificar_conDatosValidos_retornaOkYPedidoModificado() throws Exception {
        // Arrange
        PedidoDTO dto = pedidoDTO(TipoDeEntrega.MOSTRADOR, "Ana Modificada", "99887766");
        PedidoResponse responseMock = pedidoResponse(1L, "PENDIENTE", 6000.0, TipoDeEntrega.MOSTRADOR);
        when(pedidoService.modificar(eq(1L), any(PedidoDTO.class))).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(put("/api/v1/pedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(6000.0));
    }

    // ==========================================
    // PATCH /api/v1/pedidos/{id}/estado
    // ==========================================

    @Test
    @DisplayName("PATCH /{id}/estado - Debe retornar 200 OK y el pedido con nuevo estado")
    void cambiarEstado_conEstadoValido_retornaOkYEstadoActualizado() throws Exception {
        // Arrange
        PedidoResponse responseMock = pedidoResponse(1L, "ENTREGADO", 5000.0, TipoDeEntrega.MOSTRADOR);
        when(pedidoService.cambiarEstado(1L, "ENTREGADO")).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pedidos/{id}/estado", 1L)
                        .param("estado", "ENTREGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }

    // ==========================================
    // DELETE /api/v1/pedidos/{id}
    // ==========================================

    @Test
    @DisplayName("DELETE /{id} - Debe retornar 204 No Content al eliminar un pedido")
    void eliminar_conIdExistente_retornaNoContent() throws Exception {
        // Arrange
        doNothing().when(pedidoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/pedidos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).eliminar(1L);
    }
}
