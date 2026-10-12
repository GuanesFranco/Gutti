package com.grupo4.gutti.services;

import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre("Franco Guanes")
                .email("franco@gutti.com")
                .passwordHash("hashedPass")
                .rol("CLIENTE")
                .build();
    }

    @Test
    @DisplayName("Debe retornar UserDetails cuando el usuario existe por email")
    void loadUserByUsername_UsuarioExiste_RetornaUserDetails() {
        when(usuarioRepository.findByEmail("franco@gutti.com")).thenReturn(Optional.of(usuarioMock));

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("franco@gutti.com");

        assertNotNull(userDetails);
        assertEquals("franco@gutti.com", userDetails.getUsername());
        assertEquals("hashedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        verify(usuarioRepository, times(1)).findByEmail("franco@gutti.com");
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario no existe")
    void loadUserByUsername_UsuarioNoExiste_LanzaException() {
        when(usuarioRepository.findByEmail("noexiste@gutti.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                usuarioDetailsService.loadUserByUsername("noexiste@gutti.com")
        );

        assertTrue(ex.getMessage().contains("Usuario no encontrado con email: noexiste@gutti.com"));
        verify(usuarioRepository, times(1)).findByEmail("noexiste@gutti.com");
    }
}
