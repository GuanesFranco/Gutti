package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.pedido.PedidoDTO;
import com.grupo4.gutti.dtos.pedido.PedidoResponse;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.exceptions.StockInsuficienteException;
import com.grupo4.gutti.models.ItemPedido;
import com.grupo4.gutti.models.Pedido;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.PedidoRepository;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de PedidoService (lógica de pedidos).
 * Se mockean los repositorios: no se toca la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private PedidoService pedidoService;

    private static final String EMAIL = "cliente@gutti.com";

    // ---------- helpers ----------

    private Producto producto(Long id, String nombre, double precio, int stock, boolean activo) {
        return Producto.builder()
                .id(id).nombre(nombre).categoria("INFUSIONES")
                .precio(precio).stock(stock).estadoActivo(activo)
                .build();
    }

    private PedidoDTO pedidoDTO(TipoDeEntrega tipo, PedidoDTO.Item... items) {
        PedidoDTO dto = new PedidoDTO();
        dto.setTipoDeEntrega(tipo);
        dto.setNombreCliente("Ana");
        dto.setTelefono("11");
        dto.setItems(new ArrayList<>(List.of(items)));
        return dto;
    }

    private PedidoDTO.Item item(Long productoId, Integer cantidad) {
        PedidoDTO.Item it = new PedidoDTO.Item();
        it.setProductoId(productoId);
        it.setCantidad(cantidad);
        return it;
    }

    private Pedido pedidoConItem(String estado, TipoDeEntrega tipo, Producto prod, int cantidad) {
        Pedido p = new Pedido();
        p.setEstado(estado);
        p.setTipoDeEntrega(tipo);
        p.setFechaHora(LocalDateTime.now());
        ItemPedido it = new ItemPedido();
        it.setProducto(prod);
        it.setCantidad(cantidad);
        it.setPrecioUnitario(prod.getPrecio());
        it.setPedido(p);
        p.getItems().add(it);
        return p;
    }

    // ---------- registrar ----------

    @Test
    void registrar_descuentaStockYCalculaTotal() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cafe));

        PedidoResponse resp = pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 2)), EMAIL);

        assertThat(resp.getTotal()).isEqualTo(5000.0);   // 2 x 2500
        assertThat(cafe.getStock()).isEqualTo(48);       // 50 - 2
        assertThat(resp.getEstado()).isEqualTo(Pedido.ESTADO_PENDIENTE);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void registrar_stockInsuficiente_lanzaExcepcion() {
        Producto cafe = producto(1L, "Café", 2500.0, 1, true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cafe));

        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 99999)), EMAIL))
                .isInstanceOf(StockInsuficienteException.class);

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void registrar_productoInexistente_lanza404() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void registrar_usuarioInexistente_lanza404() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void registrar_productoInactivo_lanzaIllegalArgument() {
        Producto inactivo = producto(1L, "Café", 2500.0, 50, false);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registrar_sinItems_lanzaIllegalArgument() {
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registrar_tipoEntregaNull_lanzaIllegalArgument() {
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(null, item(1L, 1)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registrar_cantidadInvalida_lanzaIllegalArgument() {
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 0)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---------- consultar ----------

    @Test
    void consultar_sinPedidos_devuelveListaVacia() {
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of());

        List<PedidoResponse> resultado = pedidoService.consultar(null, null, null);

        assertThat(resultado).isEmpty();   // 200 con [], NO 404
    }

    @Test
    void consultar_rangoFechasInvalido_lanzaIllegalArgument() {
        assertThatThrownBy(() ->
                pedidoService.consultar(null, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consultar_filtraPorTipoDeEntrega() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido mostrador = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1);
        Pedido delivery = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.DELIVERY, cafe, 1);
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(mostrador, delivery));

        List<PedidoResponse> soloMostrador = pedidoService.consultar(TipoDeEntrega.MOSTRADOR, null, null);

        assertThat(soloMostrador).hasSize(1);
        assertThat(soloMostrador.get(0).getTipoDeEntrega()).isEqualTo(TipoDeEntrega.MOSTRADOR);
    }

    // ---------- recaudación ----------

    @Test
    void calcularRecaudacion_sumaLosTotales() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido p1 = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 2); // 5000
        Pedido p2 = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1); // 2500
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(p1, p2));

        Double recaudacion = pedidoService.calcularRecaudacion(null, null, null);

        assertThat(recaudacion).isEqualTo(7500.0);
    }

    @Test
    void calcularRecaudacion_sinPedidos_devuelveCero() {
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of());

        assertThat(pedidoService.calcularRecaudacion(null, null, null)).isEqualTo(0.0);
    }

    // ---------- obtener / modificar / eliminar / estado ----------

    @Test
    void obtenerPorId_inexistente_lanza404() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.obtenerPorId(99L), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void modificar_pedidoEntregado_lanza409() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido entregado = pedidoConItem(Pedido.ESTADO_ENTREGADO, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(entregado));

        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.modificar(1L, pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1))),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void eliminar_pedidoEntregado_lanza409() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido entregado = pedidoConItem(Pedido.ESTADO_ENTREGADO, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(entregado));

        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.eliminar(1L), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(pedidoRepository, never()).delete(any());
    }

    @Test
    void cambiarEstado_invalido_lanzaIllegalArgument() {
        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, "VOLANDO"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(pedidoRepository, never()).findById(anyLong());
    }

    @Test
    void cambiarEstado_valido_actualizaYGuarda() {
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido pendiente = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pendiente));

        PedidoResponse resp = pedidoService.cambiarEstado(1L, Pedido.ESTADO_ENTREGADO);

        assertThat(resp.getEstado()).isEqualTo(Pedido.ESTADO_ENTREGADO);
        verify(pedidoRepository).save(pendiente);
    }
}
