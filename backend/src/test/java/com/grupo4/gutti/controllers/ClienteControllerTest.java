package com.grupo4.gutti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.dtos.cliente.ClienteDTO;
import com.grupo4.gutti.dtos.cliente.ClienteResponse;
import com.grupo4.gutti.services.ClienteService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de ClienteController")
class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        auth = new UsernamePasswordAuthenticationToken("franco@gutti.com", null, Collections.emptyList());
    }

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private ClienteDTO clienteDTO(String nombre, String telefono, String direccion) {
        return ClienteDTO.builder()
                .nombre(nombre)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }

    private ClienteResponse clienteResponse(Long id, String nombre, String email, String telefono, String direccion) {
        return ClienteResponse.builder()
                .id(id)
                .nombre(nombre)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }

    // ==========================================
    // GET /api/v1/clientes/mi-perfil
    // ==========================================

    @Test
    @DisplayName("GET /mi-perfil - Debe retornar 200 OK y los datos del perfil autenticado")
    void obtenerMiPerfil_conUsuarioAutenticado_retornaOkYPerfil() throws Exception {
        // Arrange
        ClienteResponse responseMock = clienteResponse(1L, "Franco Guanes", "franco@gutti.com", "11223344", "Calle 123");
        when(clienteService.obtenerMiPerfil("franco@gutti.com")).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/mi-perfil").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Franco Guanes"))
                .andExpect(jsonPath("$.email").value("franco@gutti.com"));
    }

    // ==========================================
    // PUT /api/v1/clientes/mi-perfil
    // ==========================================

    @Test
    @DisplayName("PUT /mi-perfil - Debe retornar 200 OK al actualizar el perfil propio")
    void actualizarMiPerfil_conDatosValidos_retornaOkYPerfilActualizado() throws Exception {
        // Arrange
        ClienteDTO dto = clienteDTO("Franco Modificado", "99887766", "Avenida Siempre Viva 742");
        ClienteResponse responseMock = clienteResponse(1L, "Franco Modificado", "franco@gutti.com", "99887766", "Avenida Siempre Viva 742");

        when(clienteService.actualizarMiPerfil(eq("franco@gutti.com"), any(ClienteDTO.class)))
                .thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(put("/api/v1/clientes/mi-perfil")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Franco Modificado"))
                .andExpect(jsonPath("$.telefono").value("99887766"))
                .andExpect(jsonPath("$.direccion").value("Avenida Siempre Viva 742"));
    }

    // ==========================================
    // GET /api/v1/clientes
    // ==========================================

    @Test
    @DisplayName("GET / - Debe retornar 200 OK y la lista de todos los clientes")
    void obtenerTodos_conClientesExistentes_retornaOkYLista() throws Exception {
        // Arrange
        ClienteResponse c1 = clienteResponse(1L, "Franco", "franco@gutti.com", "1122", "Dir 1");
        ClienteResponse c2 = clienteResponse(2L, "Ana", "ana@gutti.com", "3344", "Dir 2");
        when(clienteService.obtenerTodos()).thenReturn(List.of(c1, c2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Franco"))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }

    // ==========================================
    // GET /api/v1/clientes/{id}
    // ==========================================

    @Test
    @DisplayName("GET /{id} - Debe retornar 200 OK y el cliente cuando el ID existe")
    void obtenerPorId_conIdExistente_retornaOkYCliente() throws Exception {
        // Arrange
        Long id = 1L;
        ClienteResponse responseMock = clienteResponse(id, "Franco", "franco@gutti.com", "1122", "Dir 1");
        when(clienteService.obtenerPorId(id)).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("franco@gutti.com"));
    }

    // ==========================================
    // PUT /api/v1/clientes/{id}
    // ==========================================

    @Test
    @DisplayName("PUT /{id} - Debe retornar 200 OK al actualizar un cliente por ID")
    void actualizar_conDatosValidos_retornaOkYClienteActualizado() throws Exception {
        // Arrange
        Long id = 1L;
        ClienteDTO dto = clienteDTO("Franco Admin Mod", "12345678", "Nueva Calle 456");
        ClienteResponse responseMock = clienteResponse(id, "Franco Admin Mod", "franco@gutti.com", "12345678", "Nueva Calle 456");

        when(clienteService.actualizar(eq(id), any(ClienteDTO.class))).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(put("/api/v1/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.telefono").value("12345678"));
    }
}
