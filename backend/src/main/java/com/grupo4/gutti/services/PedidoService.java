/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Lógica de negocio para registrar, consultar y modificar pedidos.
 */

package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.pedido.HistorialPedidosDTO;
import com.grupo4.gutti.dtos.pedido.ItemPedidoDTO;
import com.grupo4.gutti.dtos.pedido.ModificarPedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoRespuestaDTO;
import com.grupo4.gutti.dtos.pedido.RegistrarPedidoDTO;
import com.grupo4.gutti.enums.EstadoPedido;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.exceptions.OperacionNoPermitidaException;
import com.grupo4.gutti.exceptions.RecursoNoEncontradoException;
import com.grupo4.gutti.mappers.PedidoMapper;
import com.grupo4.gutti.models.Pedido;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.PedidoRepository;
import com.grupo4.gutti.repositories.PedidoSpecifications;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public static final String MENSAJE_SIN_PEDIDOS = "No se encontraron pedidos";

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

    /**
     * Consulta el historial de ventas. Todos los filtros son opcionales y se pueden combinar.
     *
     * @param tipoDeEntrega filtra por MOSTRADOR o DELIVERY (null = todos)
     * @param desde         fecha inicial inclusive (null = sin límite)
     * @param hasta         fecha final inclusive (null = sin límite)
     * @return los pedidos ordenados del más reciente al más antiguo, con la recaudación total;
     *         si no hay resultados, la lista vacía y el mensaje "No se encontraron pedidos"
     * @throws IllegalArgumentException si la fecha "desde" es posterior a "hasta"
     */
    @Transactional(readOnly = true)
    public HistorialPedidosDTO consultar(TipoDeEntrega tipoDeEntrega, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }

        List<Specification<Pedido>> filtros = new ArrayList<>();
        if (tipoDeEntrega != null) {
            filtros.add(PedidoSpecifications.conTipoDeEntrega(tipoDeEntrega));
        }
        if (desde != null) {
            filtros.add(PedidoSpecifications.desde(desde.atStartOfDay()));
        }
        if (hasta != null) {
            filtros.add(PedidoSpecifications.antesDe(hasta.plusDays(1).atStartOfDay()));
        }

        List<PedidoRespuestaDTO> pedidos = pedidoRepository
                .findAll(Specification.allOf(filtros), Sort.by(Sort.Direction.DESC, "fechaHora"))
                .stream()
                .map(PedidoMapper::aRespuesta)
                .toList();
        double recaudacionTotal = pedidos.stream().mapToDouble(PedidoRespuestaDTO::getTotal).sum();

        return HistorialPedidosDTO.builder()
                .pedidos(pedidos)
                .recaudacionTotal(recaudacionTotal)
                .mensaje(pedidos.isEmpty() ? MENSAJE_SIN_PEDIDOS : null)
                .build();
    }

    /**
     * @param id identificador del pedido
     * @return el detalle del pedido
     * @throws RecursoNoEncontradoException si el pedido no existe
     */
    @Transactional(readOnly = true)
    public PedidoRespuestaDTO obtenerPorId(Long id) {
        return PedidoMapper.aRespuesta(buscarPedido(id));
    }

    /**
     * Modifica los datos de entrega y los productos de un pedido. Repone el stock de los productos
     * anteriores, descuenta el de los nuevos y recalcula el total.
     *
     * @param id  identificador del pedido
     * @param dto nuevos datos de entrega y lista completa de productos
     * @return el pedido actualizado
     * @throws OperacionNoPermitidaException si el pedido ya fue entregado
     * @throws com.grupo4.gutti.exceptions.StockInsuficienteException si un producto no tiene stock;
     *         en ese caso no se guarda ningún cambio
     */
    @Transactional
    public PedidoRespuestaDTO modificar(Long id, ModificarPedidoDTO dto) {
        Pedido pedido = buscarPedido(id);
        pedido.validarQueSePuedeModificar();
        validarDatosDeEntrega(dto.getTipoDeEntrega(), dto.getDireccionEntrega());

        pedido.setTipoDeEntrega(dto.getTipoDeEntrega());
        pedido.setNombreCliente(dto.getNombreCliente());
        pedido.setTelefono(dto.getTelefono());
        pedido.setDireccionEntrega(dto.getDireccionEntrega());

        // Primero se devuelve el stock de los ítems actuales y después se descuenta el de los nuevos.
        // Si algún producto no tiene stock, la excepción deshace toda la transacción.
        pedido.quitarItemsReponiendoStock();
        agregarItems(pedido, dto.getItems());

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido {} modificado. Nuevo total: {}", id, guardado.calcularCostoTotal());
        return PedidoMapper.aRespuesta(guardado);
    }

    /**
     * Cambia el estado de un pedido, por ejemplo de PENDIENTE a ENTREGADO.
     *
     * @param id          identificador del pedido
     * @param nuevoEstado estado a asignar
     * @return el pedido con el estado actualizado
     */
    @Transactional
    public PedidoRespuestaDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPedido(id);
        log.info("Pedido {}: estado {} -> {}", id, pedido.getEstado(), nuevoEstado);
        pedido.setEstado(nuevoEstado);
        return PedidoMapper.aRespuesta(pedidoRepository.save(pedido));
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

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido con id " + id));
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
