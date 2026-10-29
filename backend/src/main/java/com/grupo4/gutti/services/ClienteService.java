package com.grupo4.gutti.services;

import com.grupo4.gutti.config.DetallesUsuario;
import com.grupo4.gutti.dtos.cliente.ClienteDTO;
import com.grupo4.gutti.dtos.cliente.ClienteResponse;
import com.grupo4.gutti.models.Cliente;
import com.grupo4.gutti.repositories.ClienteRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;




@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    private ClienteResponse mapearAClienteResponse(Cliente cliente) {
        String nombre = cliente.getUsuario() != null ? cliente.getUsuario().getNombre() : null;
        String email = cliente.getUsuario() != null ? cliente.getUsuario().getEmail() : null;
        java.util.UUID usuarioId = cliente.getUsuario() != null ? cliente.getUsuario().getId() : null;

        return ClienteResponse.builder()
                .id(cliente.getId())
                .usuarioId(usuarioId)
                .nombre(nombre)
                .email(email)
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .fechaCreacion(cliente.getFechaCreacion())
                .fechaModificacion(cliente.getFechaModificacion())
                .build();
    }

    private Cliente buscarClientePorId(Long id) {
        if (id == null) {
            log.warn("El ID de cliente es obligatorio");
            throw new ResponseStatusException(BAD_REQUEST, "El campo ID es obligatorio");
        }
        return clienteRepository.findById(id).orElseThrow(() -> {
            log.warn("El cliente con ID {} no existe", id);
            return new ResponseStatusException(NOT_FOUND, "El cliente no existe");
        });
    }

    public List<ClienteResponse> obtenerTodos() {
        log.info("Consultando todos los clientes registrados");
        return clienteRepository.findAll().stream()
                .map(this::mapearAClienteResponse)
                .toList();
    }

    public ClienteResponse obtenerPorId(Long id) {
        Cliente cliente = buscarClientePorId(id);
        return mapearAClienteResponse(cliente);
    }

    public ClienteResponse obtenerMiPerfil(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El email del usuario es obligatorio");
        }
        Cliente cliente = clienteRepository.findByUsuarioEmail(email).orElseThrow(() -> {
            log.warn("No se encontró perfil de cliente para el email: {}", email);
            return new ResponseStatusException(NOT_FOUND, "Perfil de cliente no encontrado");
        });
        return mapearAClienteResponse(cliente);
    }

    public ClienteResponse actualizarMiPerfil(String email, ClienteDTO dto) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El email del usuario es obligatorio");
        }
        Cliente cliente = clienteRepository.findByUsuarioEmail(email).orElseThrow(() -> {
            log.warn("No se encontró perfil de cliente para el email: {}", email);
            return new ResponseStatusException(NOT_FOUND, "Perfil de cliente no encontrado");
        });

        cliente.setTelefono(dto.getTelefono().trim());
        cliente.setDireccion(dto.getDireccion().trim());

        String nuevoToken = null;
        if (cliente.getUsuario() != null) {
            if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
                cliente.getUsuario().setNombre(dto.getNombre().trim());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                String nuevoEmail = dto.getEmail().trim();
                if (!nuevoEmail.equalsIgnoreCase(cliente.getUsuario().getEmail())) {
                    if (usuarioRepository.existsByEmail(nuevoEmail)) {
                        throw new ResponseStatusException(CONFLICT, "El email ya se encuentra en uso");
                    }
                    cliente.getUsuario().setEmail(nuevoEmail);
                    usuarioRepository.save(cliente.getUsuario());
                    java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
                    extraClaims.put("rol", cliente.getUsuario().getRol());
                    nuevoToken = jwtService.generarToken(extraClaims, new DetallesUsuario(cliente.getUsuario()));
                }
            }
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Perfil de cliente actualizado para el usuario: {}", email);

        ClienteResponse respuesta = mapearAClienteResponse(clienteGuardado);
        respuesta.setToken(nuevoToken);
        return respuesta;
    }

    public ClienteResponse actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = buscarClientePorId(id);

        cliente.setTelefono(dto.getTelefono().trim());
        cliente.setDireccion(dto.getDireccion().trim());

        if (cliente.getUsuario() != null) {
            if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
                cliente.getUsuario().setNombre(dto.getNombre().trim());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                String nuevoEmail = dto.getEmail().trim();
                if (!nuevoEmail.equalsIgnoreCase(cliente.getUsuario().getEmail())) {
                    if (usuarioRepository.existsByEmail(nuevoEmail)) {
                        throw new ResponseStatusException(CONFLICT, "El email ya se encuentra en uso");
                    }
                    cliente.getUsuario().setEmail(nuevoEmail);
                    usuarioRepository.save(cliente.getUsuario());
                }
            }
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente con ID {} actualizado correctamente", id);

        return mapearAClienteResponse(clienteGuardado);
    }
}
