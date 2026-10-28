import { describe, expect, it } from 'vitest';
import { esHelado, GUSTOS_HELADO, maxGustosPermitidos } from '../src/utils/helados.js';
import { cambiarCantidad, itemsDelCarrito } from '../src/utils/pedido.js';

describe('helados utils', () => {
  it('detecta productos de heladería', () => {
    expect(esHelado({ categoria: 'HELADOS', nombre: 'Pote 1/4 kg' })).toBe(true);
    expect(esHelado({ categoria: 'TORTAS', nombre: 'Chocotorta' })).toBe(false);
  });

  it('determina el máximo de gustos según el tamaño', () => {
    expect(maxGustosPermitidos({ nombre: 'Pote de Helado 1/4 kg' })).toBe(2);
    expect(maxGustosPermitidos({ nombre: 'Pote de Helado 1/2 kg' })).toBe(3);
    expect(maxGustosPermitidos({ nombre: 'Pote de Helado 1 kg' })).toBe(4);
  });

  it('incluye una lista completa de gustos artesanales', () => {
    expect(GUSTOS_HELADO.length).toBeGreaterThanOrEqual(10);
    expect(GUSTOS_HELADO).toContain('Dulce de leche con brownie');
    expect(GUSTOS_HELADO).toContain('Menta granizada');
  });

  it('guarda distintas combinaciones de gustos como líneas separadas en el carrito', () => {
    const pote = { id: 6, nombre: 'Pote de Helado 1/4 kg', precio: 9000 };
    let carrito = {};
    carrito = cambiarCantidad(carrito, pote, 1, 'Dulce de leche con brownie, Menta granizada');
    carrito = cambiarCantidad(carrito, pote, 1, 'Frutos rojos, Chocolate amargo');

    const items = itemsDelCarrito(carrito);
    expect(items).toHaveLength(2);
    expect(items[0]).toEqual({
      productoId: 6,
      cantidad: 1,
      gustos: 'Dulce de leche con brownie, Menta granizada',
    });
    expect(items[1]).toEqual({
      productoId: 6,
      cantidad: 1,
      gustos: 'Frutos rojos, Chocolate amargo',
    });
  });
});

