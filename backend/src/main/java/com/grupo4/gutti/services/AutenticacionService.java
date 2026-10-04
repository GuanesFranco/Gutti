package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.AutenticacionRespuestaDTO;
import com.grupo4.gutti.dtos.IniciarSesionDTO;
import com.grupo4.gutti.dtos.RegistroClienteDTO;
import com.grupo4.gutti.models.Cliente;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.config.DetallesUsuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void registrarCliente(RegistroClienteDTO dto) {
        log.info("Iniciando registro para el email: {}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("El email {} ya está en uso", dto.getEmail());
            throw new IllegalArgumentException("El email ingresado ya se encuentra registrado.");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .rol("CLIENTE")
                .build();

        Cliente cliente = Cliente.builder()
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .usuario(usuario)
                .build();
        usuario.setCliente(cliente);

        usuarioRepository.save(usuario);

        log.info("Cliente registrado exitosamente con ID: {}", usuario.getId());
    }

    public AutenticacionRespuestaDTO iniciarSesion(IniciarSesionDTO dto) {
        log.info("Iniciando sesión para el email: {}", dto.getEmail());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        String jwtToken = jwtService.generarToken(new DetallesUsuario(usuario));
        
        log.info("Sesión iniciada correctamente, token generado para: {}", dto.getEmail());
        return new AutenticacionRespuestaDTO(jwtToken);
    }
}
