package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de AutenticacionService")
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

    private Usuario usuarioMock(String nombre, String email, String rol) {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .email(email)
                .passwordHash("passwordEncriptado")
                .rol(rol)
                .build();
    }

    // ==========================================
    // REGISTRAR CLIENTE
    // ==========================================

    @Test
    @DisplayName("registrarCliente() - Debe guardar el usuario con contraseña hasheada y rol CLIENTE")
    void registrarCliente_conDatosValidos_guardaUsuarioCorrectamente() {
        // Arrange
        RegistroClienteDTO dto = registroDTO("Franco Guanes", "franco@gutti.com", "password123", "1122334455", "Av. Siempre Viva 123");
        when(usuarioRepository.existsByEmail("franco@gutti.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashSeguro123");

        // Act
        autenticacionService.registrarCliente(dto);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());

        Usuario guardado = usuarioCaptor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Franco Guanes");
        assertThat(guardado.getEmail()).isEqualTo("franco@gutti.com");
        assertThat(guardado.getPasswordHash()).isEqualTo("hashSeguro123");
        assertThat(guardado.getRol()).isEqualTo("CLIENTE");
        assertThat(guardado.getCliente()).isNotNull();
        assertThat(guardado.getCliente().getTelefono()).isEqualTo("1122334455");
        assertThat(guardado.getCliente().getDireccion()).isEqualTo("Av. Siempre Viva 123");
    }

    @Test
    @DisplayName("registrarCliente() - Debe lanzar IllegalArgumentException si el email ya existe")
    void registrarCliente_conEmailDuplicado_lanzaIllegalArgumentException() {
        // Arrange
        RegistroClienteDTO dto = registroDTO("Franco Guanes", "franco@gutti.com", "password123", "1122334455", "Av. Siempre Viva 123");
        when(usuarioRepository.existsByEmail("franco@gutti.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> autenticacionService.registrarCliente(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email ingresado ya se encuentra registrado.");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==========================================
    // INICIAR SESIÓN
    // ==========================================

    @Test
    @DisplayName("iniciarSesion() - Debe autenticar y retornar token JWT cuando credenciales son válidas")
    void iniciarSesion_conCredencialesValidas_retornaTokenJwt() {
        // Arrange
        IniciarSesionDTO dto = loginDTO("franco@gutti.com", "password123");
        Usuario usuario = usuarioMock("Franco Guanes", "franco@gutti.com", "CLIENTE");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail("franco@gutti.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(any(UserDetails.class))).thenReturn("token.jwt.generado");

        // Act
        AutenticacionRespuestaDTO respuesta = autenticacionService.iniciarSesion(dto);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getToken()).isEqualTo("token.jwt.generado");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generarToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("iniciarSesion() - Debe lanzar BadCredentialsException cuando las credenciales son incorrectas")
    void iniciarSesion_conPasswordIncorrecto_lanzaBadCredentialsException() {
        // Arrange
        IniciarSesionDTO dto = loginDTO("franco@gutti.com", "passwordIncorrecto");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> autenticacionService.iniciarSesion(dto))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    @DisplayName("iniciarSesion() - Debe lanzar IllegalArgumentException cuando el usuario no existe")
    void iniciarSesion_conUsuarioInexistente_lanzaIllegalArgumentException() {
        // Arrange
        IniciarSesionDTO dto = loginDTO("noexiste@gutti.com", "password123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail("noexiste@gutti.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> autenticacionService.iniciarSesion(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no encontrado.");

        verify(jwtService, never()).generarToken(any());
    }
}
