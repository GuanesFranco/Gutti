package com.grupo4.gutti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.exceptions.GlobalExceptionHandler;
import com.grupo4.gutti.services.AutenticacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de AutenticacionController")
class AutenticacionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private AutenticacionController autenticacionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(autenticacionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private RegistroClienteDTO registroDTO(String nombre, String email, String password, String telefono, String direccion) {
        RegistroClienteDTO dto = new RegistroClienteDTO();
        dto.setNombre(nombre);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setTelefono(telefono);
        dto.setDireccion(direccion);
        return dto;
    }

    private IniciarSesionDTO loginDTO(String email, String password) {
        IniciarSesionDTO dto = new IniciarSesionDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    // ==========================================
    // POST /api/v1/auth/registro
    // ==========================================

    @Test
    @DisplayName("registrarCliente() - Debe retornar 201 Created cuando los datos de registro son válidos")
    void registrarCliente_conDatosValidos_retorna201() throws Exception {
        // Arrange
        RegistroClienteDTO dto = registroDTO("Franco Guanes", "franco@gutti.com", "12345678", "1122334455", "Calle 123");
        doNothing().when(autenticacionService).registrarCliente(any(RegistroClienteDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(autenticacionService, times(1)).registrarCliente(any(RegistroClienteDTO.class));
    }

    @Test
    @DisplayName("registrarCliente() - Debe retornar 400 Bad Request cuando el email ya está en uso")
    void registrarCliente_conEmailExistente_retorna400() throws Exception {
        // Arrange
        RegistroClienteDTO dto = registroDTO("Franco Guanes", "existente@gutti.com", "12345678", "1122334455", "Calle 123");
        doThrow(new IllegalArgumentException("El email ingresado ya se encuentra registrado."))
                .when(autenticacionService).registrarCliente(any(RegistroClienteDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email ingresado ya se encuentra registrado."));
    }

    @Test
    @DisplayName("registrarCliente() - Debe retornar 400 Bad Request cuando la contraseña tiene menos de 8 caracteres")
    void registrarCliente_conPasswordCorta_retorna400() throws Exception {
        // Arrange
        RegistroClienteDTO dto = registroDTO("Franco Guanes", "franco@gutti.com", "123", "1122334455", "Calle 123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(autenticacionService, never()).registrarCliente(any());
    }

    @Test
    @DisplayName("registrarCliente() - Debe retornar 400 Bad Request cuando faltan campos obligatorios")
    void registrarCliente_conCamposFaltantes_retorna400() throws Exception {
        // Arrange
        RegistroClienteDTO dto = new RegistroClienteDTO();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(autenticacionService, never()).registrarCliente(any());
    }

    // ==========================================
    // POST /api/v1/auth/iniciar-sesion
    // ==========================================

    @Test
    @DisplayName("iniciarSesion() - Debe retornar 200 OK con el token cuando las credenciales son correctas")
    void iniciarSesion_conCredencialesValidas_retorna200ConToken() throws Exception {
        // Arrange
        IniciarSesionDTO dto = loginDTO("franco@gutti.com", "12345678");
        when(autenticacionService.iniciarSesion(any(IniciarSesionDTO.class)))
                .thenReturn(new AutenticacionRespuestaDTO("token.jwt.valido"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.valido"));

        verify(autenticacionService, times(1)).iniciarSesion(any(IniciarSesionDTO.class));
    }

    @Test
    @DisplayName("iniciarSesion() - Debe retornar 401 Unauthorized cuando las credenciales son inválidas")
    void iniciarSesion_conCredencialesInvalidas_retorna401() throws Exception {
        // Arrange
        IniciarSesionDTO dto = loginDTO("franco@gutti.com", "passwordIncorrecto");
        when(autenticacionService.iniciarSesion(any(IniciarSesionDTO.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas. Verifica tu email y contraseña."));
    }

    @Test
    @DisplayName("iniciarSesion() - Debe retornar 400 Bad Request cuando el email tiene formato incorrecto")
    void iniciarSesion_conEmailFormatoInvalido_retorna400() throws Exception {
        // Arrange
        IniciarSesionDTO dto = loginDTO("email-invalido", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(autenticacionService, never()).iniciarSesion(any());
    }

    @Test
    @DisplayName("iniciarSesion() - Debe retornar 400 Bad Request cuando los campos son enviados vacíos")
    void iniciarSesion_conCamposVacios_retorna400() throws Exception {
        // Arrange
        IniciarSesionDTO dto = loginDTO("", "");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(autenticacionService, never()).iniciarSesion(any());
    }
}
