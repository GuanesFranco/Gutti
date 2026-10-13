package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AutenticacionService autenticacionService;

    private RegistroClienteDTO registroDTO;
    private IniciarSesionDTO loginDTO;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        registroDTO = new RegistroClienteDTO();
        registroDTO.setNombre("Franco Guanes");
        registroDTO.setEmail("franco@gutti.com");
        registroDTO.setPassword("password123");
        registroDTO.setTelefono("1122334455");
        registroDTO.setDireccion("Av. Siempre Viva 123");

        loginDTO = new IniciarSesionDTO();
        loginDTO.setEmail("franco@gutti.com");
        loginDTO.setPassword("password123");

        usuarioMock = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre("Franco Guanes")
                .email("franco@gutti.com")
                .passwordHash("hashedPassword")
                .rol("CLIENTE")
                .build();
    }

    @Nested
    @DisplayName("Pruebas del método registrarCliente()")
    class RegistrarClienteTests {

        @Test
        @DisplayName("Debe registrar un cliente exitosamente cuando el email no existe")
        void registrarCliente_Exitoso() {
            // Arrange
            when(usuarioRepository.existsByEmail(registroDTO.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(registroDTO.getPassword())).thenReturn("encodedPassword123");

            // Act
            autenticacionService.registrarCliente(registroDTO);

            // Assert
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());

            Usuario usuarioGuardado = usuarioCaptor.getValue();
            assertEquals("Franco Guanes", usuarioGuardado.getNombre());
            assertEquals("franco@gutti.com", usuarioGuardado.getEmail());
            assertEquals("encodedPassword123", usuarioGuardado.getPasswordHash());
            assertEquals("CLIENTE", usuarioGuardado.getRol());

            assertNotNull(usuarioGuardado.getCliente());
            assertEquals("1122334455", usuarioGuardado.getCliente().getTelefono());
            assertEquals("Av. Siempre Viva 123", usuarioGuardado.getCliente().getDireccion());
            assertEquals(usuarioGuardado, usuarioGuardado.getCliente().getUsuario());
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya está en uso")
        void registrarCliente_EmailDuplicado_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.existsByEmail(registroDTO.getEmail())).thenReturn(true);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    autenticacionService.registrarCliente(registroDTO)
            );

            assertEquals("El email ingresado ya se encuentra registrado.", ex.getMessage());
            verify(usuarioRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("Pruebas del método iniciarSesion()")
    class IniciarSesionTests {

        @Test
        @DisplayName("Debe iniciar sesión y retornar token JWT cuando las credenciales son válidas")
        void iniciarSesion_Exitoso() {
            // Arrange
            String tokenEsperado = "jwt.token.de.prueba";
            when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuarioMock));
            when(jwtService.generarToken(any(UserDetails.class))).thenReturn(tokenEsperado);

            // Act
            AutenticacionRespuestaDTO respuesta = autenticacionService.iniciarSesion(loginDTO);

            // Assert
            assertNotNull(respuesta);
            assertEquals(tokenEsperado, respuesta.getToken());
            verify(authenticationManager, times(1))
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(usuarioRepository, times(1)).findByEmail(loginDTO.getEmail());
            verify(jwtService, times(1)).generarToken(any(UserDetails.class));
        }

        @Test
        @DisplayName("Debe propagar BadCredentialsException cuando las credenciales son incorrectas")
        void iniciarSesion_CredencialesInvalidas_LanzaBadCredentialsException() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () ->
                    autenticacionService.iniciarSesion(loginDTO)
            );

            verify(usuarioRepository, never()).findByEmail(any());
            verify(jwtService, never()).generarToken(any());
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException si el usuario no existe tras la autenticación")
        void iniciarSesion_UsuarioNoEncontrado_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    autenticacionService.iniciarSesion(loginDTO)
            );

            assertEquals("Usuario no encontrado.", ex.getMessage());
            verify(jwtService, never()).generarToken(any());
        }
    }
}
