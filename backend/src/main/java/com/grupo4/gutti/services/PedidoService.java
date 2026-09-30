/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Lógica de negocio para registrar pedidos.
 */

package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.pedido.ItemPedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoRespuestaDTO;
import com.grupo4.gutti.dtos.pedido.RegistrarPedidoDTO;
import com.grupo4.gutti.enums.EstadoPedido;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.exceptions.RecursoNoEncontradoException;
import com.grupo4.gutti.mappers.PedidoMapper;
import com.grupo4.gutti.models.Pedido;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.PedidoRepository;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de pedidos: aplica las reglas de negocio de las ventas.
 *
 * @author Alexis Monte
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra un pedido nuevo: descuenta el stock de cada producto y calcula el total a pagar.
     *
     * @param dto          datos del pedido (tipo de entrega, cliente y productos)
     * @param emailUsuario email del administrador autenticado que registra la venta
     * @return el pedido registrado con sus ítems y el total
     * @throws com.grupo4.gutti.exceptions.StockInsuficienteException si algún producto no tiene stock suficiente
     * @throws RecursoNoEncontradoException si un producto o el usuario no existen
     * @throws IllegalArgumentException si el pedido no tiene productos o falta la dirección de un delivery
     */
    @Transactional
    public PedidoRespuestaDTO registrar(RegistrarPedidoDTO dto, String emailUsuario) {
        log.info("Registrando pedido de tipo {}", dto.getTipoDeEntrega());
        validarDatosDeEntrega(dto.getTipoDeEntrega(), dto.getDireccionEntrega());

        Pedido pedido = Pedido.builder()
                .fechaHora(LocalDateTime.now())
                .estado(EstadoPedido.PENDIENTE)
                .tipoDeEntrega(dto.getTipoDeEntrega())
                .nombreCliente(dto.getNombreCliente())
                .telefono(dto.getTelefono())
                .direccionEntrega(dto.getDireccionEntrega())
                .usuario(buscarUsuario(emailUsuario))
                .build();

        agregarItems(pedido, dto.getItems());

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido {} registrado. Total: {}", guardado.getId(), guardado.calcularCostoTotal());
        return PedidoMapper.aRespuesta(guardado);
    }

    private void agregarItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }
        for (ItemPedidoDTO itemDTO : items) {
            pedido.agregarItem(buscarProductoActivo(itemDTO.getProductoId()), itemDTO.getCantidad());
        }
    }

    private void validarDatosDeEntrega(TipoDeEntrega tipoDeEntrega, String direccionEntrega) {
        if (tipoDeEntrega == TipoDeEntrega.DELIVERY && (direccionEntrega == null || direccionEntrega.isBlank())) {
            throw new IllegalArgumentException(
                    "La dirección de entrega es obligatoria para pedidos con envío a domicilio.");
        }
    }

    private Producto buscarProductoActivo(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + productoId));
        if (!producto.isEstadoActivo()) {
            throw new IllegalArgumentException(
                    "El producto '" + producto.getNombre() + "' está deshabilitado y no se puede vender.");
        }
        return producto;
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + email));
    }
}
