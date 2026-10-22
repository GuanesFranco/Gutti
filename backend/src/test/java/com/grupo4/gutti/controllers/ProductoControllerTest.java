package com.grupo4.gutti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.dtos.Producto.FilterProductoRequest;
import com.grupo4.gutti.dtos.Producto.ModifiedStateProductoResponse;
import com.grupo4.gutti.dtos.Producto.ProductoDTO;
import com.grupo4.gutti.dtos.Producto.ProductoResponse;
import com.grupo4.gutti.enums.CategoriasEnum;
import com.grupo4.gutti.services.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de ProductoController")
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
    }

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private ProductoDTO productoDTO(String nombre, CategoriasEnum categoria, Double precio, Integer stock) {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(nombre);
        dto.setCategoria(categoria);
        dto.setPrecio(precio);
        dto.setStock(stock);
        return dto;
    }

    private ProductoResponse productoResponse(Long id, String nombre, Double precio) {
        return ProductoResponse.builder()
                .id(id)
                .nombre(nombre)
                .precio(precio)
                .build();
    }

    private ModifiedStateProductoResponse modifiedStateResponse(boolean estadoActivo) {
        return ModifiedStateProductoResponse.builder()
                .fechaModificacion(LocalDateTime.now())
                .estadoActivo(estadoActivo)
                .build();
    }

    // ==========================================
    // GET /api/v1/productos/obtenerPorId/{id}
    // ==========================================

    @Test
    @DisplayName("GET /obtenerPorId/{id} - Debe retornar 200 OK y el producto cuando existe")
    void obtenerPorId_conIdExistente_retornaOkYProducto() throws Exception {
        // Arrange
        Long id = 1L;
        ProductoResponse responseMock = productoResponse(id, "Café Especial", 1500.0);
        when(productoService.obtenerPorId(id)).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/productos/obtenerPorId/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Café Especial"));
    }

    // ==========================================
    // POST /api/v1/productos/crearProducto
    // ==========================================

    @Test
    @DisplayName("POST /crearProducto - Debe retornar 201 Created cuando el DTO es válido")
    void crearProducto_conDatosValidos_retornaCreatedYProducto() throws Exception {
        // Arrange
        ProductoDTO dto = productoDTO("Café Cortado", CategoriasEnum.INFUSIONES, 500.0, 20);
        ProductoResponse responseMock = productoResponse(1L, "Café Cortado", 500.0);
        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Café Cortado"));
    }

    @Test
    @DisplayName("POST /crearProducto - Debe retornar 400 Bad Request cuando el precio es negativo")
    void crearProducto_conPrecioNegativo_retornaBadRequest() throws Exception {
        // Arrange
        ProductoDTO dto = productoDTO("Café Malo", CategoriasEnum.INFUSIONES, -50.0, 10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /crearProducto - Debe retornar 400 Bad Request cuando el nombre está vacío")
    void crearProducto_conNombreVacio_retornaBadRequest() throws Exception {
        // Arrange
        ProductoDTO dto = productoDTO("", CategoriasEnum.INFUSIONES, 500.0, 10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // DELETE /api/v1/productos/eliminarProducto/{id}
    // ==========================================

    @Test
    @DisplayName("DELETE /eliminarProducto/{id} - Debe retornar 200 OK y desactivar el producto")
    void eliminarProducto_conIdExistente_retornaOkYDesactiva() throws Exception {
        // Arrange
        Long id = 1L;
        ModifiedStateProductoResponse responseMock = modifiedStateResponse(false);
        when(productoService.eliminarProducto(id)).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/productos/eliminarProducto/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActivo").value(false));
    }

    // ==========================================
    // PATCH /api/v1/productos/activarProducto/{id}
    // ==========================================

    @Test
    @DisplayName("PATCH /activarProducto/{id} - Debe retornar 200 OK y activar el producto")
    void activarProducto_conIdExistente_retornaOkYActiva() throws Exception {
        // Arrange
        Long id = 1L;
        ModifiedStateProductoResponse responseMock = modifiedStateResponse(true);
        when(productoService.activarProducto(id)).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/productos/activarProducto/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActivo").value(true));
    }

    // ==========================================
    // GET /api/v1/productos/consultarproductos
    // ==========================================

    @Test
    @DisplayName("GET /consultarproductos - Debe retornar 200 OK con la lista de productos filtrados")
    void filtroProductos_conParametrosValidos_retornaOkYLista() throws Exception {
        // Arrange
        ProductoResponse responseMock = productoResponse(1L, "Café Filtrado", 800.0);
        when(productoService.filtroProductosDinamico(any(FilterProductoRequest.class)))
                .thenReturn(List.of(responseMock));

        // Act & Assert
        mockMvc.perform(get("/api/v1/productos/consultarproductos")
                        .param("nombre", "Café"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Café Filtrado"));
    }

    // ==========================================
    // PUT /api/v1/productos/modificarProducto/{id}
    // ==========================================

    @Test
    @DisplayName("PUT /modificarProducto/{id} - Debe retornar 200 OK al actualizar un producto correctamente")
    void actualizarProducto_conDatosValidos_retornaOkYProductoActualizado() throws Exception {
        // Arrange
        Long id = 1L;
        ProductoDTO dto = productoDTO("Café Actualizado", CategoriasEnum.INFUSIONES, 1200.0, 15);
        ProductoResponse responseMock = productoResponse(id, "Café Actualizado", 1200.0);
        when(productoService.actualizarProducto(any(ProductoDTO.class), eq(id))).thenReturn(responseMock);

        // Act & Assert
        mockMvc.perform(put("/api/v1/productos/modificarProducto/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Café Actualizado"));
    }

    @Test
    @DisplayName("PUT /modificarProducto/{id} - Debe retornar 400 Bad Request si los datos enviados son inválidos")
    void actualizarProducto_conDatosInvalidos_retornaBadRequest() throws Exception {
        // Arrange
        Long id = 1L;
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("");
        dto.setPrecio(-10.0);

        // Act & Assert
        mockMvc.perform(put("/api/v1/productos/modificarProducto/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}