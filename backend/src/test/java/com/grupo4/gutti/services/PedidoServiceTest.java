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
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private static final String EMAIL = "cliente@gutti.com";

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private Producto producto(Long id, String nombre, double precio, int stock, boolean activo) {
        return Producto.builder()
                .id(id)
                .nombre(nombre)
                .categoria("INFUSIONES")
                .precio(precio)
                .stock(stock)
                .estadoActivo(activo)
                .build();
    }

    private PedidoDTO.Item item(Long productoId, Integer cantidad) {
        PedidoDTO.Item it = new PedidoDTO.Item();
        it.setProductoId(productoId);
        it.setCantidad(cantidad);
        return it;
    }

    private PedidoDTO pedidoDTO(TipoDeEntrega tipo, PedidoDTO.Item... items) {
        PedidoDTO dto = new PedidoDTO();
        dto.setTipoDeEntrega(tipo);
        dto.setNombreCliente("Ana");
        dto.setTelefono("11");
        dto.setItems(new ArrayList<>(List.of(items)));
        return dto;
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

    // ==========================================
    // REGISTRAR PEDIDO
    // ==========================================

    @Test
    @DisplayName("registrar() - Debe descontar stock y calcular total correctamente en pedido válido")
    void registrar_conDatosValidos_descuentaStockYCalculaTotal() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cafe));

        // Act
        PedidoResponse resp = pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 2)), EMAIL);

        // Assert
        assertThat(resp.getTotal()).isEqualTo(5000.0);
        assertThat(cafe.getStock()).isEqualTo(48);
        assertThat(resp.getEstado()).isEqualTo(Pedido.ESTADO_PENDIENTE);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("registrar() - Debe lanzar StockInsuficienteException cuando no alcanza el stock")
    void registrar_conStockInsuficiente_lanzaStockInsuficienteException() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 1, true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cafe));

        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 99999)), EMAIL))
                .isInstanceOf(StockInsuficienteException.class);

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrar() - Debe lanzar 404 NOT_FOUND cuando el producto no existe")
    void registrar_conProductoInexistente_lanzaNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL),
                ResponseStatusException.class);

        // Assert
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("registrar() - Debe lanzar 404 NOT_FOUND cuando el usuario no existe")
    void registrar_conUsuarioInexistente_lanzaNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL),
                ResponseStatusException.class);

        // Assert
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("registrar() - Debe lanzar IllegalArgumentException si el producto está inactivo")
    void registrar_conProductoInactivo_lanzaIllegalArgumentException() {
        // Arrange
        Producto inactivo = producto(1L, "Café", 2500.0, 50, false);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(inactivo));

        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registrar() - Debe lanzar IllegalArgumentException cuando la lista de items está vacía")
    void registrar_sinItems_lanzaIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registrar() - Debe lanzar IllegalArgumentException cuando tipoDeEntrega es null")
    void registrar_conTipoEntregaNulo_lanzaIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(null, item(1L, 1)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registrar() - Debe lanzar IllegalArgumentException cuando la cantidad del item es menor o igual a cero")
    void registrar_conCantidadInvalida_lanzaIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.registrar(pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 0)), EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ==========================================
    // CONSULTAR PEDIDOS
    // ==========================================

    @Test
    @DisplayName("consultar() - Debe retornar lista vacía (200 OK) cuando no hay pedidos registrados")
    void consultar_sinPedidos_retornaListaVacia() {
        // Arrange
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of());

        // Act
        List<PedidoResponse> resultado = pedidoService.consultar(null, null, null);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("consultar() - Debe lanzar IllegalArgumentException cuando desde es posterior a hasta")
    void consultar_conRangoFechasInvalido_lanzaIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() ->
                pedidoService.consultar(null, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("consultar() - Debe filtrar pedidos por tipo de entrega correctamente")
    void consultar_conFiltroTipoEntrega_filtraCorrectamente() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido mostrador = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1);
        Pedido delivery = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.DELIVERY, cafe, 1);
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(mostrador, delivery));

        // Act
        List<PedidoResponse> soloMostrador = pedidoService.consultar(TipoDeEntrega.MOSTRADOR, null, null);

        // Assert
        assertThat(soloMostrador).hasSize(1);
        assertThat(soloMostrador.get(0).getTipoDeEntrega()).isEqualTo(TipoDeEntrega.MOSTRADOR);
    }

    // ==========================================
    // CALCULAR RECAUDACIÓN
    // ==========================================

    @Test
    @DisplayName("calcularRecaudacion() - Debe sumar correctamente los totales de los pedidos")
    void calcularRecaudacion_conPedidosExistentes_sumaTotalesCorrectamente() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido p1 = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 2);
        Pedido p2 = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(p1, p2));

        // Act
        Double recaudacion = pedidoService.calcularRecaudacion(null, null, null);

        // Assert
        assertThat(recaudacion).isEqualTo(7500.0);
    }

    @Test
    @DisplayName("calcularRecaudacion() - Debe retornar 0.0 cuando no hay pedidos")
    void calcularRecaudacion_sinPedidos_retornaCero() {
        // Arrange
        when(pedidoRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of());

        // Act & Assert
        assertThat(pedidoService.calcularRecaudacion(null, null, null)).isEqualTo(0.0);
    }

    // ==========================================
    // OBTENER / MODIFICAR / ELIMINAR / ESTADO
    // ==========================================

    @Test
    @DisplayName("obtenerPorId() - Debe lanzar 404 NOT_FOUND cuando el pedido no existe")
    void obtenerPorId_conIdInexistente_lanzaNotFound() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.obtenerPorId(99L), ResponseStatusException.class);

        // Assert
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("modificar() - Debe lanzar 409 CONFLICT cuando el pedido ya está ENTREGADO")
    void modificar_conPedidoYaEntregado_lanzaConflict() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido entregado = pedidoConItem(Pedido.ESTADO_ENTREGADO, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(entregado));

        // Act
        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.modificar(1L, pedidoDTO(TipoDeEntrega.MOSTRADOR, item(1L, 1))),
                ResponseStatusException.class);

        // Assert
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminar() - Debe lanzar 409 CONFLICT cuando el pedido ya está ENTREGADO")
    void eliminar_conPedidoYaEntregado_lanzaConflict() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido entregado = pedidoConItem(Pedido.ESTADO_ENTREGADO, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(entregado));

        // Act
        ResponseStatusException ex = catchThrowableOfType(
                () -> pedidoService.eliminar(1L), ResponseStatusException.class);

        // Assert
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(pedidoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("cambiarEstado() - Debe lanzar IllegalArgumentException si el nuevo estado no es válido")
    void cambiarEstado_conEstadoInvalido_lanzaIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, "VOLANDO"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(pedidoRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("cambiarEstado() - Debe actualizar el estado y guardar cuando el estado es válido")
    void cambiarEstado_conEstadoValido_actualizaYGuardaPedido() {
        // Arrange
        Producto cafe = producto(1L, "Café", 2500.0, 50, true);
        Pedido pendiente = pedidoConItem(Pedido.ESTADO_PENDIENTE, TipoDeEntrega.MOSTRADOR, cafe, 1);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pendiente));

        // Act
        PedidoResponse resp = pedidoService.cambiarEstado(1L, Pedido.ESTADO_ENTREGADO);

        // Assert
        assertThat(resp.getEstado()).isEqualTo(Pedido.ESTADO_ENTREGADO);
        verify(pedidoRepository).save(pendiente);
    }
}
