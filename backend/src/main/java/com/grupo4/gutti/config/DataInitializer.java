package com.grupo4.gutti.config;

import com.grupo4.gutti.models.Cliente;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        //Crear Administrador 
        String adminPass = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");
        if (!usuarioRepository.existsByEmail("admin@gutti.com")) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin Gutti")
                    .email("admin@gutti.com")
                    .passwordHash(passwordEncoder.encode(adminPass))
                    .rol("ADMIN")
                    .build();
            
            usuarioRepository.save(admin);
            log.info("Administrador creado: admin@gutti.com / [OCULTO]");
        }

        //Crear Cliente 
        String clientPass = System.getenv().getOrDefault("CLIENT_PASSWORD", "cliente123");
        if (!usuarioRepository.existsByEmail("cliente@gutti.com")) {
            Usuario usuarioCliente = Usuario.builder()
                    .nombre("Cliente Prueba")
                    .email("cliente@gutti.com")
                    .passwordHash(passwordEncoder.encode(clientPass))
                    .rol("CLIENTE")
                    .build();
            
            Cliente cliente = Cliente.builder()
                    .telefono("1122334455")
                    .direccion("Calle Falsa 123")
                    .usuario(usuarioCliente)
                    .build();
            
            usuarioCliente.setCliente(cliente);
            
            usuarioRepository.save(usuarioCliente);
            log.info("Cliente creado: cliente@gutti.com / [OCULTO]");
        }
    }
}
