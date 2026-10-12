package com.grupo4.gutti;

import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;
import com.grupo4.gutti.dtos.Producto.ModifiedStateProductoResponse;
import com.grupo4.gutti.dtos.Producto.ProductoDTO;
import com.grupo4.gutti.dtos.Producto.ProductoResponse;
import com.grupo4.gutti.enums.CategoriasEnum;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;
import com.grupo4.gutti.services.ProductoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    

    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void crearProductoExitosamente() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Especial");
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(1500.0);
        dto.setStock(10);
        dto.setDescripcion("Café colombiano");

        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        
        Producto productoGuardado = Producto.builder()
                .id(1L)
                .nombre("Café Especial")
                .categoria("INFUSIONES")
                .precio(1500.0)
                .stock(10)
                .estadoActivo(true)
                .build();

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        ProductoResponse resultado = productoService.crearProducto(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Café Especial", resultado.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar CONFLICT al intentar crear un producto con nombre duplicado")
    void crearProductoDuplicadoLanzaExcepcion() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Duplicado");

        when(productoRepository.existsByNombre(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.crearProducto(dto);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("No se puede crear un producto ya existente", exception.getReason());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    

    @Test
    @DisplayName("Debería retornar un producto al buscar por ID existente")
    void obtenerPorIdExitosamente() {
        Long id = 1L;
        Producto productoMock = Producto.builder().id(id).nombre("Te Verde").precio(20.0).estadoActivo(true).build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        ProductoResponse resultado = productoService.obtenerPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería lanzar BAD_REQUEST si el ID es nulo al buscar")
    void obtenerPorIdNuloLanzaExcepcion() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.obtenerPorId(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El campo id es obligatorio", exception.getReason());
    }

    @Test
    @DisplayName("Debería lanzar NOT_FOUND si el ID no existe")
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.obtenerPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("El producto no existe", exception.getReason());
    }

    

    @Test
    @DisplayName("Debería desactivar un producto exitosamente")
    void eliminarProductoExitosamente() {
        Long id = 1L;
        Producto productoMock = Producto.builder().id(id).nombre("Medialuna").estadoActivo(true).build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        ModifiedStateProductoResponse resultado = productoService.eliminarProducto(id);

        assertNotNull(resultado);
        assertFalse(resultado.isEstadoActivo());
    }

    @Test
    @DisplayName("Debería lanzar CONFLICT si el producto a eliminar ya está inactivo")
    void eliminarProductoYaInactivoLanzaExcepcion() {
        Long id = 1L;
        Producto productoMock = Producto.builder().id(id).nombre("Medialuna").estadoActivo(false).build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.eliminarProducto(id);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("El producto ya se encuentra desactivado", exception.getReason());
    }

    

    @Test
    @DisplayName("Debería activar un producto inactivo exitosamente")
    void activarProductoExitosamente() {
        Long id = 1L;
        Producto productoMock = Producto.builder().id(id).nombre("Factura").estadoActivo(false).build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        ModifiedStateProductoResponse resultado = productoService.activarProducto(id);

        assertNotNull(resultado);
        assertTrue(resultado.isEstadoActivo());
    }

    @Test
    @DisplayName("Debería lanzar CONFLICT si el producto a activar ya está activo")
    void activarProductoYaActivoLanzaExcepcion() {
        Long id = 1L;
        Producto productoMock = Producto.builder().id(id).nombre("Factura").estadoActivo(true).build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productoService.activarProducto(id);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("El producto ya esta activo", exception.getReason());
    }

    

    @Test
    @DisplayName("Debería retornar una lista de productos filtrados")
    void filtroProductosDinamicoExitosamente() {
        FilterProductoRequest request = new FilterProductoRequest();
        request.setNombre("Café");

        Producto productoMock = Producto.builder().id(1L).nombre("Café").precio(20.5).estadoActivo(true).build();
        when(productoRepository.filterQuery(request)).thenReturn(List.of(productoMock));

        List<ProductoResponse> resultados = productoService.filtroProductosDinamico(request);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Café", resultados.get(0).getNombre());
    }

   

    @Test
    @DisplayName("Debería actualizar un producto exitosamente")
    void actualizarProductoExitosamente() {
        Long id = 1L;
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Actualizado");
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(2000.0);
        dto.setStock(15);

        Producto productoExistente = Producto.builder()
                .id(id)
                .nombre("Café Viejo")
                .estadoActivo(true)
                .build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        ProductoResponse resultado = productoService.actualizarProducto(dto, id);

        assertNotNull(resultado);
        assertEquals("Café Actualizado", resultado.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
}