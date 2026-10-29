package com.grupo4.gutti.services;

import com.grupo4.gutti.dtos.Producto.*;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import static org.springframework.http.HttpStatus.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    private ProductoResponse mapearAProductoResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .descripcion(producto.getDescripcion())
                .imagenUrl(producto.getImagenUrl())
                .estadoActivo(producto.isEstadoActivo())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaModificacion(producto.getFechaModificacion())
                .build();
    }

    private Producto buscarProductoPorId(Long id) {
        if (id == null) {
            log.warn("El id es obligatorio");
            throw new ResponseStatusException(BAD_REQUEST, "El campo id es obligatorio");
        }
        return productoRepository.findById(id).orElseThrow(() -> {
            log.warn("El producto consultado no existe");
            return new ResponseStatusException(NOT_FOUND, "El producto no existe");
        });
    }

    private String limpiarImagenUrl(String imagenUrl) {
        if (imagenUrl == null || imagenUrl.isBlank()) {
            return null;
        }
        return imagenUrl.trim();
    }

    public ProductoResponse crearProducto(ProductoDTO request) {
        if (productoRepository.existsByNombre(request.getNombre().trim())) {
            log.warn("El producto {} ya existe", request.getNombre());
            throw new ResponseStatusException(CONFLICT, "No se puede crear un producto ya existente");
        }

        Producto producto = Producto.builder()
                .nombre(request.getNombre().trim())
                .descripcion(request.getDescripcion())
                .imagenUrl(limpiarImagenUrl(request.getImagenUrl()))
                .categoria(request.getCategoria().name())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .estadoActivo(true)
                .build();

        Producto productoGuardado = productoRepository.save(producto);

        return mapearAProductoResponse(productoGuardado);
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = buscarProductoPorId(id);
        return mapearAProductoResponse(producto);
    }

    public ModifiedStateProductoResponse eliminarProducto(Long id) {
        Producto producto = buscarProductoPorId(id);

        if (!producto.isEstadoActivo()) {
            log.warn("El producto ya esta inactivo");
            throw new ResponseStatusException(CONFLICT, "El producto ya se encuentra desactivado");
        }

        producto.setEstadoActivo(false);

        return ModifiedStateProductoResponse.builder()
                .fechaModificacion(producto.getFechaModificacion())
                .estadoActivo(producto.isEstadoActivo())
                .build();
    }

    public ModifiedStateProductoResponse activarProducto(Long id) {
        Producto producto = buscarProductoPorId(id);

        if (producto.isEstadoActivo()) {
            log.warn("El producto ya esta activo");
            throw new ResponseStatusException(CONFLICT, "El producto ya esta activo");
        }

        producto.setEstadoActivo(true);

        return ModifiedStateProductoResponse.builder()
                .fechaModificacion(producto.getFechaModificacion())
                .estadoActivo(producto.isEstadoActivo())
                .build();
    }

    public List<ProductoResponse> filtroProductosDinamico(FilterProductoRequest request) {
        List<Producto> query = productoRepository.filterQuery(request);

        return query.stream()
                .map(this::mapearAProductoResponse)
                .toList();
    }

    public ProductoResponse actualizarProducto(ProductoDTO request, Long id) {
        Producto productoAActualizar = buscarProductoPorId(id);

        if (!request.getNombre().trim().equalsIgnoreCase(productoAActualizar.getNombre())) {
            if (productoRepository.existsByNombre(request.getNombre().trim())) {
                throw new ResponseStatusException(
                        CONFLICT, "Ya existe otro producto con el nombre: " + request.getNombre()
                );
            }
        }

        if (!productoAActualizar.isEstadoActivo()) {
            log.warn("No se puede actualizar un producto inactivo");
            throw new ResponseStatusException(CONFLICT, "No se puede actualizar un producto inactivo");
        }

        productoAActualizar.setNombre(request.getNombre().trim());
        productoAActualizar.setCategoria(request.getCategoria().name());
        productoAActualizar.setPrecio(request.getPrecio());
        productoAActualizar.setStock(request.getStock());
        productoAActualizar.setDescripcion(request.getDescripcion());
        productoAActualizar.setImagenUrl(limpiarImagenUrl(request.getImagenUrl()));

        Producto productoGuardado = productoRepository.save(productoAActualizar);

        return mapearAProductoResponse(productoGuardado);
    }
}
