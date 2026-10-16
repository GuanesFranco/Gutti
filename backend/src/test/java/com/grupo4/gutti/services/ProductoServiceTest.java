package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;
import com.grupo4.gutti.dtos.Producto.ModifiedStateProductoResponse;
import com.grupo4.gutti.dtos.Producto.ProductoDTO;
import com.grupo4.gutti.dtos.Producto.ProductoResponse;
import com.grupo4.gutti.enums.CategoriasEnum;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de ProductoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private ProductoDTO productoDTO(String nombre, CategoriasEnum categoria, Double precio, Integer stock, String descripcion) {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(nombre);
        dto.setCategoria(categoria);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setDescripcion(descripcion);
        return dto;
    }

    private Producto productoMock(Long id, String nombre, Double precio, boolean estadoActivo) {
        return Producto.builder()
                .id(id)
                .nombre(nombre)
                .precio(precio)
                .estadoActivo(estadoActivo)
                .build();
    }

    private Producto productoMock(Long id, String nombre, String categoria, Double precio, Integer stock, boolean estadoActivo) {
        return Producto.builder()
                .id(id)
                .nombre(nombre)
                .categoria(categoria)
                .precio(precio)
                .stock(stock)
                .estadoActivo(estadoActivo)
                .build();
    }

    // ==========================================
    // CREAR PRODUCTO
    // ==========================================

    @Test
    @DisplayName("crearProducto() - Debe crear un producto exitosamente cuando los datos son válidos")
    void crearProducto_conDatosValidos_creaProductoExitosamente() {
        // Arrange
        ProductoDTO dto = productoDTO("Café Especial", CategoriasEnum.INFUSIONES, 1500.0, 10, "Café colombiano");
        Producto productoGuardado = productoMock(1L, "Café Especial", "INFUSIONES", 1500.0, 10, true);

        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // Act
        ProductoResponse resultado = productoService.crearProducto(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Café Especial");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("crearProducto() - Debe lanzar CONFLICT cuando se intenta crear un producto con nombre duplicado")
    void crearProducto_conNombreDuplicado_lanzaConflict() {
        // Arrange
        ProductoDTO dto = productoDTO("Café Duplicado", null, null, null, null);
        when(productoRepository.existsByNombre(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productoService.crearProducto(dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(rse.getReason()).isEqualTo("No se puede crear un producto ya existente");
                });

        verify(productoRepository, never()).save(any(Producto.class));
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================

    @Test
    @DisplayName("obtenerPorId() - Debe retornar el producto cuando el ID existe")
    void obtenerPorId_conIdExistente_retornaProductoExitosamente() {
        // Arrange
        Long id = 1L;
        Producto producto = productoMock(id, "Te Verde", 20.0, true);
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act
        ProductoResponse resultado = productoService.obtenerPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("obtenerPorId() - Debe lanzar BAD_REQUEST si el ID proporcionado es nulo")
    void obtenerPorId_conIdNulo_lanzaBadRequest() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> productoService.obtenerPorId(null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).isEqualTo("El campo id es obligatorio");
                });
    }

    @Test
    @DisplayName("obtenerPorId() - Debe lanzar NOT_FOUND cuando el ID no existe")
    void obtenerPorId_conIdInexistente_lanzaNotFound() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("El producto no existe");
                });
    }

    // ==========================================
    // ELIMINAR / DESACTIVAR PRODUCTO
    // ==========================================

    @Test
    @DisplayName("eliminarProducto() - Debe desactivar un producto activo exitosamente")
    void eliminarProducto_conProductoActivo_desactivaProductoExitosamente() {
        // Arrange
        Long id = 1L;
        Producto producto = productoMock(id, "Medialuna", 10.0, true);
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act
        ModifiedStateProductoResponse resultado = productoService.eliminarProducto(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isEstadoActivo()).isFalse();
    }

    @Test
    @DisplayName("eliminarProducto() - Debe lanzar CONFLICT si el producto ya se encuentra inactivo")
    void eliminarProducto_conProductoYaInactivo_lanzaConflict() {
        // Arrange
        Long id = 1L;
        Producto producto = productoMock(id, "Medialuna", 10.0, false);
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act & Assert
        assertThatThrownBy(() -> productoService.eliminarProducto(id))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(rse.getReason()).isEqualTo("El producto ya se encuentra desactivado");
                });
    }

    // ==========================================
    // ACTIVAR PRODUCTO
    // ==========================================

    @Test
    @DisplayName("activarProducto() - Debe activar un producto inactivo exitosamente")
    void activarProducto_conProductoInactivo_activaProductoExitosamente() {
        // Arrange
        Long id = 1L;
        Producto producto = productoMock(id, "Factura", 10.0, false);
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act
        ModifiedStateProductoResponse resultado = productoService.activarProducto(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isEstadoActivo()).isTrue();
    }

    @Test
    @DisplayName("activarProducto() - Debe lanzar CONFLICT si el producto ya se encuentra activo")
    void activarProducto_conProductoYaActivo_lanzaConflict() {
        // Arrange
        Long id = 1L;
        Producto producto = productoMock(id, "Factura", 10.0, true);
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act & Assert
        assertThatThrownBy(() -> productoService.activarProducto(id))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(rse.getReason()).isEqualTo("El producto ya esta activo");
                });
    }

    // ==========================================
    // FILTRO DINÁMICO DE PRODUCTOS
    // ==========================================

    @Test
    @DisplayName("filtroProductosDinamico() - Debe retornar lista de productos según criterios de filtro")
    void filtroProductosDinamico_conFiltrosValidos_retornaListaProductos() {
        // Arrange
        FilterProductoRequest request = new FilterProductoRequest();
        request.setNombre("Café");

        Producto producto = productoMock(1L, "Café", 20.5, true);
        when(productoRepository.filterQuery(request)).thenReturn(List.of(producto));

        // Act
        List<ProductoResponse> resultados = productoService.filtroProductosDinamico(request);

        // Assert
        assertThat(resultados).isNotNull().hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Café");
    }

    // ==========================================
    // ACTUALIZAR PRODUCTO
    // ==========================================

    @Test
    @DisplayName("actualizarProducto() - Debe actualizar los datos del producto exitosamente")
    void actualizarProducto_conDatosValidos_actualizaYRetornaProducto() {
        // Arrange
        Long id = 1L;
        ProductoDTO dto = productoDTO("Café Actualizado", CategoriasEnum.INFUSIONES, 2000.0, 15, null);
        Producto productoExistente = productoMock(id, "Café Viejo", 1500.0, true);

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        // Act
        ProductoResponse resultado = productoService.actualizarProducto(dto, id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Café Actualizado");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
}