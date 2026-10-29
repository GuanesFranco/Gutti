package com.grupo4.gutti.config;

import com.grupo4.gutti.enums.CategoriasEnum;
import com.grupo4.gutti.models.Producto;
import com.grupo4.gutti.repositories.ProductoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Carga los productos de la tienda que falten. A los que ya están no los toca, así el
// administrador puede editarlos o darlos de baja sin que vuelvan al reiniciar.
@Component
@RequiredArgsConstructor
@Slf4j
public class SemillaProductos {

    private final ProductoRepository productoRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void cargar() {
        List<Producto> faltantes = crearProductos().stream()
                .filter(producto -> !productoRepository.existsByNombre(producto.getNombre()))
                .toList();
        if (faltantes.isEmpty()) {
            return;
        }

        productoRepository.saveAll(faltantes);
        log.info("Datos semilla: {} productos de la tienda cargados.", faltantes.size());
    }

    private List<Producto> crearProductos() {
        return List.of(
                producto("Docena de churros", CategoriasEnum.PANADERIA, 11000.0, 40,
                        "Con azúcar, recién hechos"),
                producto("Docena de churros rellenos de dulce de leche", CategoriasEnum.PANADERIA, 13500.0, 30,
                        "Rellenos al momento"),
                producto("Docena de medialunas de manteca", CategoriasEnum.PANADERIA, 10000.0, 30,
                        "Recién horneadas"),
                producto("Docena de chipá", CategoriasEnum.PANADERIA, 6500.0, 25,
                        "Pan de queso calentito"),
                producto("Pote de Helado 1/4 kg", CategoriasEnum.HELADOS, 9000.0, 30,
                        "Hasta 2 gustos a elección"),
                producto("Pote de Helado 1/2 kg", CategoriasEnum.HELADOS, 16000.0, 25,
                        "Hasta 3 gustos a elección"),
                producto("Pote de Helado 1 kg", CategoriasEnum.HELADOS, 28000.0, 20,
                        "Hasta 4 gustos a elección"),
                producto("Café Expreso", CategoriasEnum.INFUSIONES, 2500.0, 50,
                        "Auténtico café expreso italiano"),
                producto("Tostado de Miga", CategoriasEnum.SANGUCHES, 4500.0, 20,
                        "Clásico tostado de jamón y queso"),
                producto("Porción Chocotorta", CategoriasEnum.TORTAS, 5500.0, 15,
                        "La mejor chocotorta")
        );
    }

    private Producto producto(String nombre, CategoriasEnum categoria, Double precio, Integer stock,
                              String descripcion) {
        return Producto.builder()
                .nombre(nombre)
                .categoria(categoria.name())
                .precio(precio)
                .stock(stock)
                .descripcion(descripcion)
                .estadoActivo(true)
                .build();
    }
}
