package com.grupo4.gutti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.exceptions.GlobalExceptionHandler;
import com.grupo4.gutti.services.AutenticacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Pruebas de POST /api/v1/auth/registro")
    class RegistroTests {

        @Test
        @DisplayName("Debe retornar 201 Created cuando los datos de registro son válidos")
        void registrarCliente_DatosValidos_Retorna201() throws Exception {
            RegistroClienteDTO dto = new RegistroClienteDTO();
            dto.setNombre("Franco Guanes");
            dto.setEmail("franco@gutti.com");
            dto.setPassword("12345678");
            dto.setTelefono("1122334455");
            dto.setDireccion("Calle 123");

            doNothing().when(autenticacionService).registrarCliente(any(RegistroClienteDTO.class));

            mockMvc.perform(post("/api/v1/auth/registro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            verify(autenticacionService, times(1)).registrarCliente(any(RegistroClienteDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 Bad Request cuando el email ya está en uso")
        void registrarCliente_EmailExistente_Retorna400() throws Exception {
            RegistroClienteDTO dto = new RegistroClienteDTO();
            dto.setNombre("Franco Guanes");
            dto.setEmail("existente@gutti.com");
            dto.setPassword("12345678");
            dto.setTelefono("1122334455");
            dto.setDireccion("Calle 123");

            doThrow(new IllegalArgumentException("El email ingresado ya se encuentra registrado."))
                    .when(autenticacionService).registrarCliente(any(RegistroClienteDTO.class));

            mockMvc.perform(post("/api/v1/auth/registro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("El email ingresado ya se encuentra registrado."));
        }

        @Test
        @DisplayName("Debe retornar 400 Bad Request cuando la contraseña tiene menos de 8 caracteres")
        void registrarCliente_PasswordCorta_Retorna400() throws Exception {
            RegistroClienteDTO dto = new RegistroClienteDTO();
            dto.setNombre("Franco Guanes");
            dto.setEmail("franco@gutti.com");
            dto.setPassword("123"); // Inválida (< 8 caracteres)
            dto.setTelefono("1122334455");
            dto.setDireccion("Calle 123");

            mockMvc.perform(post("/api/v1/auth/registro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());

            verify(autenticacionService, never()).registrarCliente(any());
        }

        @Test
        @DisplayName("Debe retornar 400 Bad Request cuando faltan campos obligatorios")
        void registrarCliente_CamposFaltantes_Retorna400() throws Exception {
            RegistroClienteDTO dto = new RegistroClienteDTO(); // Vacío

            mockMvc.perform(post("/api/v1/auth/registro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());

            verify(autenticacionService, never()).registrarCliente(any());
        }
    }

    @Nested
    @DisplayName("Pruebas de POST /api/v1/auth/iniciar-sesion")
    class IniciarSesionTests {

        @Test
        @DisplayName("Debe retornar 200 OK con el token cuando las credenciales son correctas")
        void iniciarSesion_CredencialesValidas_Retorna200ConToken() throws Exception {
            IniciarSesionDTO dto = new IniciarSesionDTO();
            dto.setEmail("franco@gutti.com");
            dto.setPassword("12345678");

            when(autenticacionService.iniciarSesion(any(IniciarSesionDTO.class)))
                    .thenReturn(new AutenticacionRespuestaDTO("token.jwt.valido"));

            mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token.jwt.valido"));

            verify(autenticacionService, times(1)).iniciarSesion(any(IniciarSesionDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 401 Unauthorized cuando las credenciales son inválidas")
        void iniciarSesion_CredencialesInvalidas_Retorna401() throws Exception {
            IniciarSesionDTO dto = new IniciarSesionDTO();
            dto.setEmail("franco@gutti.com");
            dto.setPassword("passwordIncorrecto");

            when(autenticacionService.iniciarSesion(any(IniciarSesionDTO.class)))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Credenciales inválidas. Verifica tu email y contraseña."));
        }

        @Test
        @DisplayName("Debe retornar 400 Bad Request cuando el email tiene formato incorrecto")
        void iniciarSesion_EmailFormatoInvalido_Retorna400() throws Exception {
            IniciarSesionDTO dto = new IniciarSesionDTO();
            dto.setEmail("email-invalido");
            dto.setPassword("password123");

            mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());

            verify(autenticacionService, never()).iniciarSesion(any());
        }

        @Test
        @DisplayName("Debe retornar 400 Bad Request cuando los campos son enviados vacíos")
        void iniciarSesion_CamposVacios_Retorna400() throws Exception {
            IniciarSesionDTO dto = new IniciarSesionDTO();
            dto.setEmail("");
            dto.setPassword("");

            mockMvc.perform(post("/api/v1/auth/iniciar-sesion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());

            verify(autenticacionService, never()).iniciarSesion(any());
        }
    }
}
