package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.cliente.ClienteDTO;
import com.grupo4.gutti.dtos.cliente.ClienteResponse;
import com.grupo4.gutti.models.Cliente;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private Cliente clienteMock(Long id, String nombre, String email, String telefono, String direccion) {
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .email(email)
                .rol("CLIENTE")
                .build();

        return Cliente.builder()
                .id(id)
                .telefono(telefono)
                .direccion(direccion)
                .usuario(usuario)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
    }

    private ClienteDTO clienteDTO(String nombre, String telefono, String direccion) {
        return ClienteDTO.builder()
                .nombre(nombre)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }

    // ==========================================
    // OBTENER TODOS
    // ==========================================

    @Test
    @DisplayName("obtenerTodos() - Debe retornar la lista de clientes registrados")
    void obtenerTodos_conClientesExistentes_retornaListaClientes() {
        // Arrange
        Cliente c1 = clienteMock(1L, "Franco", "franco@gutti.com", "11223344", "Calle 1");
        Cliente c2 = clienteMock(2L, "Ana", "ana@gutti.com", "55667788", "Calle 2");
        when(clienteRepository.findAll()).thenReturn(List.of(c1, c2));

        // Act
        List<ClienteResponse> resultado = clienteService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Franco");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Ana");
        verify(clienteRepository, times(1)).findAll();
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================

    @Test
    @DisplayName("obtenerPorId() - Debe retornar el cliente cuando el ID existe")
    void obtenerPorId_conIdExistente_retornaCliente() {
        // Arrange
        Cliente cliente = clienteMock(1L, "Franco", "franco@gutti.com", "11223344", "Calle 1");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponse resultado = clienteService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("franco@gutti.com");
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId() - Debe lanzar NOT_FOUND cuando el ID no existe")
    void obtenerPorId_conIdInexistente_lanzaNotFound() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("El cliente no existe");
                });
    }

    @Test
    @DisplayName("obtenerPorId() - Debe lanzar BAD_REQUEST cuando el ID es nulo")
    void obtenerPorId_conIdNulo_lanzaBadRequest() {
        // Act & Assert
        assertThatThrownBy(() -> clienteService.obtenerPorId(null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).isEqualTo("El campo ID es obligatorio");
                });
    }

    // ==========================================
    // OBTENER MI PERFIL
    // ==========================================

    @Test
    @DisplayName("obtenerMiPerfil() - Debe retornar el perfil del cliente logueado por email")
    void obtenerMiPerfil_conEmailExistente_retornaPerfilCliente() {
        // Arrange
        Cliente cliente = clienteMock(1L, "Franco", "franco@gutti.com", "11223344", "Calle 1");
        when(clienteRepository.findByUsuarioEmail("franco@gutti.com")).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponse resultado = clienteService.obtenerMiPerfil("franco@gutti.com");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Franco");
        assertThat(resultado.getTelefono()).isEqualTo("11223344");
        verify(clienteRepository, times(1)).findByUsuarioEmail("franco@gutti.com");
    }

    @Test
    @DisplayName("obtenerMiPerfil() - Debe lanzar NOT_FOUND cuando no existe perfil para el email")
    void obtenerMiPerfil_conEmailInexistente_lanzaNotFound() {
        // Arrange
        when(clienteRepository.findByUsuarioEmail("noexiste@gutti.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.obtenerMiPerfil("noexiste@gutti.com"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Perfil de cliente no encontrado");
                });
    }

    // ==========================================
    // ACTUALIZAR MI PERFIL
    // ==========================================

    @Test
    @DisplayName("actualizarMiPerfil() - Debe actualizar teléfono, dirección y nombre correctamente")
    void actualizarMiPerfil_conDatosValidos_actualizaYRetornaPerfil() {
        // Arrange
        Cliente cliente = clienteMock(1L, "Franco", "franco@gutti.com", "11223344", "Calle 1");
        ClienteDTO dto = clienteDTO("Franco Modificado", "99887766", "Avenida Siempre Viva 742");

        when(clienteRepository.findByUsuarioEmail("franco@gutti.com")).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ClienteResponse resultado = clienteService.actualizarMiPerfil("franco@gutti.com", dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Franco Modificado");
        assertThat(resultado.getTelefono()).isEqualTo("99887766");
        assertThat(resultado.getDireccion()).isEqualTo("Avenida Siempre Viva 742");
        verify(clienteRepository, times(1)).save(cliente);
    }

    // ==========================================
    // ACTUALIZAR POR ID (ADMIN)
    // ==========================================

    @Test
    @DisplayName("actualizar() - Debe actualizar los datos del cliente por ID correctamente")
    void actualizar_conIdExistente_actualizaYRetornaCliente() {
        // Arrange
        Cliente cliente = clienteMock(1L, "Franco", "franco@gutti.com", "11223344", "Calle 1");
        ClienteDTO dto = clienteDTO("Franco Admin Mod", "12345678", "Nueva Calle 456");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ClienteResponse resultado = clienteService.actualizar(1L, dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTelefono()).isEqualTo("12345678");
        assertThat(resultado.getDireccion()).isEqualTo("Nueva Calle 456");
        verify(clienteRepository, times(1)).save(cliente);
    }
}
