import { describe, expect, it } from 'vitest';
import { imagenDeProducto } from '../src/utils/imagenes.js';
import { rutaEditarPedido } from '../src/utils/rutas.js';

describe('imagenDeProducto', () => {
  it('elige la foto según una palabra del nombre, sin importar mayúsculas ni tildes', () => {
    expect(imagenDeProducto('Helado 1/4 kg')).toBeTruthy();
    expect(imagenDeProducto('CHIPÁ x 10')).toBeTruthy();
    expect(imagenDeProducto('Docena de Medialunas')).toBeTruthy();
  });

  it('elige la foto más específica primero', () => {
    expect(imagenDeProducto('Churros rellenos')).not.toBe(imagenDeProducto('Churros'));
  });

  it('distingue los helados por sabor', () => {
    expect(imagenDeProducto('Helado de dulce de leche')).not.toBe(imagenDeProducto('Helado 1/4 kg'));
    expect(imagenDeProducto('Helado de frutos rojos')).not.toBe(imagenDeProducto('Helado 1/4 kg'));
    expect(imagenDeProducto('Helado de menta granizada')).toBeTruthy();
  });

  it('devuelve null si no hay una foto para el producto', () => {
    expect(imagenDeProducto('Café Expreso')).toBeNull();
  });
});

describe('rutaEditarPedido', () => {
  it('arma la ruta con el id del pedido', () => {
    expect(rutaEditarPedido(7)).toBe('/pedidos/7/editar');
  });
});
