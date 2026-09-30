/*
 * Gutti - Sistema de gestión de pedidos, stock y ventas.
 * Autor: Alexis Monte
 * Fecha: 30/09/2026
 * Descripción: Pruebas unitarias de PedidoService con Mockito.
 */

package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.pedido.*;
import com.grupo4.gutti.enums.EstadoPedido;
import com.grupo4.gutti.enums.TipoDeEntrega;
import com.grupo4.gutti.exceptions.StockInsuficienteException;
import com.grupo4.gutti.models.Pedido;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.models.Usuario;
import com.grupo4.gutti.repositories.PedidoRepository;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final String EMAIL_ADMIN = "admin@gutti.com";

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Producto pizza;
    private Producto empanada;
    private Usuario admin;

    @BeforeEach
    void setUp() {
        pizza = producto(1L, "Pizza muzzarella", 5000.0, 10);
        empanada = producto(2L, "Empanada de carne", 800.0, 24);
        admin = Usuario.builder().email(EMAIL_ADMIN).nombre("Admin").rol("ADMIN").build();
    }

    @Nested
    @DisplayName("Registrar pedido")
    class Registrar {

        @Test
        @DisplayName("calcula el total y descuenta el stock de cada producto")
        void registraPedidoCalculandoTotalYDescontandoStock() {
            when(usuarioRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(admin));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(pizza));
            when(productoRepository.findById(2L)).thenReturn(Optional.of(empanada));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.MOSTRADOR, "Juan", null, null,
                    List.of(new ItemPedidoDTO(1L, 2), new ItemPedidoDTO(2L, 6)));

            PedidoRespuestaDTO respuesta = pedidoService.registrar(dto, EMAIL_ADMIN);

            assertThat(respuesta.getTotal()).isEqualTo(2 * 5000.0 + 6 * 800.0);
            assertThat(respuesta.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(respuesta.getItems()).hasSize(2);
            assertThat(pizza.getStock()).isEqualTo(8);
            assertThat(empanada.getStock()).isEqualTo(18);
        }

        @Test
        @DisplayName("diferencia pedidos de mostrador y de envío a domicilio")
        void registraPedidoDelivery() {
            when(usuarioRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(admin));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(pizza));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.DELIVERY, "Ana", "1122334455",
                    "Av. Calchaquí 6200", List.of(new ItemPedidoDTO(1L, 1)));

            PedidoRespuestaDTO respuesta = pedidoService.registrar(dto, EMAIL_ADMIN);

            assertThat(respuesta.getTipoDeEntrega()).isEqualTo(TipoDeEntrega.DELIVERY);
            assertThat(respuesta.getDireccionEntrega()).isEqualTo("Av. Calchaquí 6200");
        }

        @Test
        @DisplayName("no registra un delivery sin dirección")
        void rechazaDeliverySinDireccion() {
            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.DELIVERY, "Ana", null, " ",
                    List.of(new ItemPedidoDTO(1L, 1)));

            assertThatThrownBy(() -> pedidoService.registrar(dto, EMAIL_ADMIN))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("no registra la venta si la cantidad supera el stock")
        void rechazaStockInsuficiente() {
            when(usuarioRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(admin));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(pizza));

            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.MOSTRADOR, null, null, null,
                    List.of(new ItemPedidoDTO(1L, 11)));

            assertThatThrownBy(() -> pedidoService.registrar(dto, EMAIL_ADMIN))
                    .isInstanceOf(StockInsuficienteException.class)
                    .hasMessageContaining("Stock insuficiente");
            assertThat(pizza.getStock()).isEqualTo(10);
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("no registra un pedido sin productos")
        void rechazaPedidoSinProductos() {
            when(usuarioRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(admin));

            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.MOSTRADOR, null, null, null, List.of());

            assertThatThrownBy(() -> pedidoService.registrar(dto, EMAIL_ADMIN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("al menos un producto");
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("no permite vender un producto deshabilitado")
        void rechazaProductoDeshabilitado() {
            pizza.setEstadoActivo(false);
            when(usuarioRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(admin));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(pizza));

            RegistrarPedidoDTO dto = new RegistrarPedidoDTO(TipoDeEntrega.MOSTRADOR, null, null, null,
                    List.of(new ItemPedidoDTO(1L, 1)));

            assertThatThrownBy(() -> pedidoService.registrar(dto, EMAIL_ADMIN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deshabilitado");
        }
    }

    private static Producto producto(Long id, String nombre, double precio, int stock) {
        return Producto.builder()
                .id(id).nombre(nombre).categoria("Comidas").precio(precio).stock(stock).estadoActivo(true)
                .build();
    }

}
