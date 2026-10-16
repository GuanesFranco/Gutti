package com.grupo4.gutti.repositories;

import com.grupo4.gutti.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioEmail(String email);
    Optional<Cliente> findByUsuarioId(UUID usuarioId);
}
