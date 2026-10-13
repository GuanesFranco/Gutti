package com.grupo4.gutti.repositories;

import java.util.List;

import com.grupo4.gutti.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Spring arma la consulta a partir del nombre: todos los pedidos, del más nuevo al más viejo.
    List<Pedido> findAllByOrderByFechaHoraDesc();
}
