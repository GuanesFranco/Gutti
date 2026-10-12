package com.grupo4.gutti;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.gutti.controllers.ProductoController;
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

    

    @Test
    @DisplayName("GET /obtenerPorId/{id} - Debería retornar 200 OK y el producto si existe")
    void obtenerPorIdExitosamente() throws Exception {
        Long id = 1L;
        ProductoResponse responseMock = ProductoResponse.builder()
                .id(id)
                .nombre("Café Especial")
                .precio(1500.0)
                .build();

        when(productoService.obtenerPorId(id)).thenReturn(responseMock);

        mockMvc.perform(get("/api/v1/productos/obtenerPorId/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Café Especial"));
    }

   

    @Test
    @DisplayName("POST /crearProducto - Debería retornar 201 Created al enviar un DTO válido")
    void crearProductoExitosamente() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Cortado");
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(500.0);
        dto.setStock(20);

        ProductoResponse responseMock = ProductoResponse.builder()
                .id(1L)
                .nombre("Café Cortado")
                .precio(500.0)
                .build();

        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(responseMock);

        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Café Cortado"));
    }

    @Test
    @DisplayName("POST /crearProducto - Debería retornar 400 Bad Request si el precio es negativo")
    void crearProductoConPrecioInvalidoLanzaBadRequest() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Malo");
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(-50.0); 
        dto.setStock(10);

        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /crearProducto - Debería retornar 400 Bad Request si el nombre está en blanco")
    void crearProductoSinNombreLanzaBadRequest() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(""); 
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(500.0);
        dto.setStock(10);

        mockMvc.perform(post("/api/v1/productos/crearProducto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    

    @Test
    @DisplayName("DELETE /eliminarProducto/{id} - Debería retornar 200 OK y desactivar el producto")
    void eliminarProductoExitosamente() throws Exception {
        Long id = 1L;
        ModifiedStateProductoResponse responseMock = ModifiedStateProductoResponse.builder()
                .fechaModificacion(LocalDateTime.now())
                .estadoActivo(false)
                .build();

        when(productoService.eliminarProducto(id)).thenReturn(responseMock);

        mockMvc.perform(delete("/api/v1/productos/eliminarProducto/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActivo").value(false));
    }

   

    @Test
    @DisplayName("PATCH /activarProducto/{id} - Debería retornar 200 OK y activar el producto")
    void activarProductoExitosamente() throws Exception {
        Long id = 1L;
        ModifiedStateProductoResponse responseMock = ModifiedStateProductoResponse.builder()
                .fechaModificacion(LocalDateTime.now())
                .estadoActivo(true)
                .build();

        when(productoService.activarProducto(id)).thenReturn(responseMock);

        mockMvc.perform(patch("/api/v1/productos/activarProducto/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActivo").value(true));
    }

    

    @Test
    @DisplayName("GET /consultarproductos - Debería retornar 200 OK con la lista de productos filtrados")
    void filtroProductosExitosamente() throws Exception {
        ProductoResponse responseMock = ProductoResponse.builder()
                .id(1L)
                .nombre("Café Filtrado")
                .precio(800.0)
                .build();

        when(productoService.filtroProductosDinamico(any(FilterProductoRequest.class)))
                .thenReturn(List.of(responseMock));

        mockMvc.perform(get("/api/v1/productos/consultarproductos")
                        .param("nombre", "Café"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Café Filtrado"));
    }

    

    @Test
    @DisplayName("PUT /modificarProducto/{id} - Debería retornar 200 OK al actualizar un producto correctamente")
    void actualizarProductoExitosamente() throws Exception {
        Long id = 1L;
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Café Actualizado");
        dto.setCategoria(CategoriasEnum.INFUSIONES);
        dto.setPrecio(1200.0);
        dto.setStock(15);

        ProductoResponse responseMock = ProductoResponse.builder()
                .id(id)
                .nombre("Café Actualizado")
                .precio(1200.0)
                .build();

        when(productoService.actualizarProducto(any(ProductoDTO.class), eq(id))).thenReturn(responseMock);

        mockMvc.perform(put("/api/v1/productos/modificarProducto/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Café Actualizado"));
    }

    @Test
    @DisplayName("PUT /modificarProducto/{id} - Debería retornar 400 Bad Request si los datos enviados son inválidos")
    void actualizarProductoConDatosInvalidosLanzaBadRequest() throws Exception {
        Long id = 1L;
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(""); 
        dto.setPrecio(-10.0); 

        mockMvc.perform(put("/api/v1/productos/modificarProducto/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}