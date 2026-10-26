package com.grupo4.gutti.config;

import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de SemillaProductos")
class SemillaProductosTest {

    private static final int CANTIDAD_PRODUCTOS_SEMILLA = 10;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private SemillaProductos semillaProductos;

    @Captor
    private ArgumentCaptor<List<Producto>> productosCaptor;

    @Test
    @DisplayName("cargar() - Debe guardar los productos de la tienda activos cuando todavía no existen")
    void cargar_sinProductosSemilla_guardaTodosActivos() {
        // Arrange
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);

        // Act
        semillaProductos.cargar();

        // Assert
        verify(productoRepository).saveAll(productosCaptor.capture());
        assertThat(productosCaptor.getValue())
                .hasSize(CANTIDAD_PRODUCTOS_SEMILLA)
                .allMatch(Producto::isEstadoActivo);
    }

    @Test
    @DisplayName("cargar() - Debe guardar solo los productos que todavía no existen")
    void cargar_conUnProductoExistente_guardaSoloLosQueFaltan() {
        // Arrange
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.existsByNombre("Docena de churros")).thenReturn(true);

        // Act
        semillaProductos.cargar();

        // Assert
        verify(productoRepository).saveAll(productosCaptor.capture());
        assertThat(productosCaptor.getValue())
                .hasSize(CANTIDAD_PRODUCTOS_SEMILLA - 1)
                .noneMatch(producto -> producto.getNombre().equals("Docena de churros"));
    }

    @Test
    @DisplayName("cargar() - No debe guardar nada si ya están todos los productos")
    void cargar_conTodosLosProductosExistentes_noGuardaNada() {
        // Arrange
        when(productoRepository.existsByNombre(anyString())).thenReturn(true);

        // Act
        semillaProductos.cargar();

        // Assert
        verify(productoRepository, never()).saveAll(any());
    }
}
