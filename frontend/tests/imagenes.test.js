import { describe, expect, it } from 'vitest';
import { imagenDeProducto } from '../src/utils/imagenes.js';
import { rutaEditarPedido } from '../src/utils/rutas.js';

describe('imagenDeProducto', () => {
  it('elige la foto según el nombre, sin importar mayúsculas ni tildes', () => {
    expect(imagenDeProducto('DOCENA DE CHIPÁ')).toBeTruthy();
    expect(imagenDeProducto('Docena de Medialunas')).toBeTruthy();
  });

  it('elige la foto más específica primero', () => {
    expect(imagenDeProducto('Churros rellenos')).not.toBe(imagenDeProducto('Churros'));
  });

  it('da una foto distinta a cada producto con foto', () => {
    const nombres = [
      'Docena de churros',
      'Docena de churros rellenos de dulce de leche',
      'Docena de medialunas de manteca',
      'Docena de chipá',
      'Helado de dulce de leche con brownie 1/4 kg',
      'Helado de frutos rojos 1/4 kg',
      'Helado de menta granizada 1/4 kg',
    ];
    const fotos = nombres.map((nombre) => imagenDeProducto(nombre));
    expect(fotos.every(Boolean)).toBe(true);
    expect(new Set(fotos).size).toBe(fotos.length);
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
