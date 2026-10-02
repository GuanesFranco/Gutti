package com.grupo4.gutti.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grupo4.gutti.dtos.pedido.PedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoResponse;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.models.ItemPedido;
import com.grupo4.gutti.models.Pedido;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.PedidoRepository;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

// @Transactional: si algo falla en el medio (por ejemplo falta stock) se deshacen todos los cambios.
@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoResponse registrar(PedidoDTO datos, String emailUsuario) {
        validar(datos);

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }

        Pedido pedido = new Pedido();
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setEstado(Pedido.ESTADO_PENDIENTE);
        pedido.setTipoDeEntrega(datos.getTipoDeEntrega());
        pedido.setNombreCliente(datos.getNombreCliente());
        pedido.setTelefono(datos.getTelefono());
        pedido.setUsuario(usuario);
        agregarProductos(pedido, datos.getItems());

        pedidoRepository.save(pedido);
        return convertir(pedido);
    }

    // Historial del más nuevo al más viejo. Los filtros son opcionales: si llegan en null no se filtra por ese dato.
    public List<PedidoResponse> consultar(TipoDeEntrega tipoDeEntrega, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        List<PedidoResponse> resultado = new ArrayList<>();
        for (Pedido pedido : pedidoRepository.findAllByOrderByFechaHoraDesc()) {
            LocalDate fecha = pedido.getFechaHora().toLocalDate();
            boolean cumpleTipo = (tipoDeEntrega == null) || (pedido.getTipoDeEntrega() == tipoDeEntrega);
            boolean cumpleDesde = (desde == null) || !fecha.isBefore(desde);
            boolean cumpleHasta = (hasta == null) || !fecha.isAfter(hasta);

            if (cumpleTipo && cumpleDesde && cumpleHasta) {
                resultado.add(convertir(pedido));
            }
        }

        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos");
        }
        return resultado;
    }

    public Double calcularRecaudacion(TipoDeEntrega tipoDeEntrega, LocalDate desde, LocalDate hasta) {
        double recaudacion = 0;
        for (PedidoResponse pedido : consultar(tipoDeEntrega, desde, hasta)) {
            recaudacion = recaudacion + pedido.getTotal();
        }
        return recaudacion;
    }

    public PedidoResponse obtenerPorId(Long id) {
        return convertir(buscarPedido(id));
    }

    public PedidoResponse modificar(Long id, PedidoDTO datos) {
        Pedido pedido = buscarPedido(id);
        if (pedido.estaEntregado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede modificar un pedido ya entregado");
        }
        validar(datos);

        pedido.setTipoDeEntrega(datos.getTipoDeEntrega());
        pedido.setNombreCliente(datos.getNombreCliente());
        pedido.setTelefono(datos.getTelefono());
        reponerStock(pedido);
        pedido.getItems().clear();
        agregarProductos(pedido, datos.getItems());

        pedidoRepository.save(pedido);
        return convertir(pedido);
    }

    public PedidoResponse cambiarEstado(Long id, String estado) {
        if (!Pedido.ESTADO_PENDIENTE.equals(estado) && !Pedido.ESTADO_ENTREGADO.equals(estado)) {
            throw new IllegalArgumentException("El estado debe ser PENDIENTE o ENTREGADO");
        }

        Pedido pedido = buscarPedido(id);
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
        return convertir(pedido);
    }

    public void eliminar(Long id) {
        Pedido pedido = buscarPedido(id);
        if (pedido.estaEntregado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un pedido ya entregado.");
        }

        reponerStock(pedido);
        pedidoRepository.delete(pedido);
    }

    private void validar(PedidoDTO datos) {
        if (datos.getTipoDeEntrega() == null) {
            throw new IllegalArgumentException("El tipo de entrega es obligatorio (MOSTRADOR o DELIVERY)");
        }
        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }
        for (PedidoDTO.Item item : datos.getItems()) {
            if (item.getProductoId() == null) {
                throw new IllegalArgumentException("Falta el productoId de uno de los productos");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Ingrese una cantidad válida (mayor a cero)");
            }
        }
    }

    private void agregarProductos(Pedido pedido, List<PedidoDTO.Item> items) {
        for (PedidoDTO.Item item : items) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);
            if (producto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el producto con id " + item.getProductoId());
            }
            if (!producto.isEstadoActivo()) {
                throw new IllegalArgumentException("El producto " + producto.getNombre() + " está desactivado");
            }

            // Si no hay stock suficiente, Producto lanza StockInsuficienteException (409).
            producto.descontarStock(item.getCantidad());

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProducto(producto);
            itemPedido.setCantidad(item.getCantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            itemPedido.setPedido(pedido);
            pedido.getItems().add(itemPedido);
        }
    }

    private void reponerStock(Pedido pedido) {
        for (ItemPedido item : pedido.getItems()) {
            item.getProducto().reponerStock(item.getCantidad());
        }
    }

    private Pedido buscarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido con id " + id);
        }
        return pedido;
    }

    private PedidoResponse convertir(Pedido pedido) {
        PedidoResponse respuesta = new PedidoResponse();
        respuesta.setId(pedido.getId());
        respuesta.setFechaHora(pedido.getFechaHora());
        respuesta.setEstado(pedido.getEstado());
        respuesta.setTipoDeEntrega(pedido.getTipoDeEntrega());
        respuesta.setNombreCliente(pedido.getNombreCliente());
        respuesta.setTelefono(pedido.getTelefono());
        respuesta.setTotal(pedido.calcularCostoTotal());

        for (ItemPedido item : pedido.getItems()) {
            PedidoResponse.Item itemRespuesta = new PedidoResponse.Item();
            itemRespuesta.setNombreProducto(item.getProducto().getNombre());
            itemRespuesta.setCantidad(item.getCantidad());
            itemRespuesta.setSubtotal(item.calcularSubtotal());
            respuesta.getItems().add(itemRespuesta);
        }
        return respuesta;
    }
}
