package com.grupo4.gutti.services;

import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de UsuarioDetailsService")
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private Usuario usuarioMock(String nombre, String email, String rol) {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .email(email)
                .passwordHash("hashedPass")
                .rol(rol)
                .build();
    }

    // ==========================================
    // LOAD USER BY USERNAME
    // ==========================================

    @Test
    @DisplayName("loadUserByUsername() - Debe retornar UserDetails cuando el usuario existe por email")
    void loadUserByUsername_conEmailExistente_retornaUserDetails() {
        // Arrange
        Usuario mockUsuario = usuarioMock("Franco Guanes", "franco@gutti.com", "CLIENTE");
        when(usuarioRepository.findByEmail("franco@gutti.com")).thenReturn(Optional.of(mockUsuario));

        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("franco@gutti.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("franco@gutti.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPass");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .contains("ROLE_CLIENTE");
        verify(usuarioRepository, times(1)).findByEmail("franco@gutti.com");
    }

    @Test
    @DisplayName("loadUserByUsername() - Debe lanzar UsernameNotFoundException cuando el usuario no existe")
    void loadUserByUsername_conEmailInexistente_lanzaUsernameNotFoundException() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@gutti.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioDetailsService.loadUserByUsername("noexiste@gutti.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con email: noexiste@gutti.com");

        verify(usuarioRepository, times(1)).findByEmail("noexiste@gutti.com");
    }
}
