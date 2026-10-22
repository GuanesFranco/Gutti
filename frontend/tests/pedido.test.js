import { describe, expect, it } from 'vitest';
import {
  ajustarCantidad,
  armarLineas,
  armarEntrega,
  calcularTotal,
  cantidadesDePedido,
  sumarStockReservado,
} from '../src/utils/pedido.js';

const CAFE = { id: 1, nombre: 'Café', precio: 2500, stock: 3 };
const TOSTADO = { id: 2, nombre: 'Tostado', precio: 4500, stock: 10 };

describe('ajustarCantidad', () => {
  it('suma y resta unidades', () => {
    expect(ajustarCantidad(1, 1, CAFE.stock)).toBe(2);
    expect(ajustarCantidad(2, -1, CAFE.stock)).toBe(1);
  });

  it('no deja pedir más que el stock', () => {
    expect(ajustarCantidad(3, 1, CAFE.stock)).toBe(3);
  });

  it('no baja de cero', () => {
    expect(ajustarCantidad(0, -1, CAFE.stock)).toBe(0);
  });
});

describe('armarLineas', () => {
  it('arma una línea por producto elegido y saca los que tienen cantidad cero', () => {
    const lineas = armarLineas([CAFE, TOSTADO], { 1: 2, 2: 0 });
    expect(lineas).toEqual([{ producto: CAFE, cantidad: 2 }]);
  });
});

describe('calcularTotal', () => {
  it('suma precio por cantidad de cada línea', () => {
    const lineas = [
      { producto: CAFE, cantidad: 2 },
      { producto: TOSTADO, cantidad: 1 },
    ];
    expect(calcularTotal(lineas)).toBe(9500);
  });

  it('da cero si no hay productos', () => {
    expect(calcularTotal([])).toBe(0);
  });
});

describe('cantidadesDePedido', () => {
  it('vuelve a armar el carrito buscando cada producto por nombre', () => {
    const pedido = { items: [{ nombreProducto: 'Tostado', cantidad: 2 }] };
    expect(cantidadesDePedido(pedido, [CAFE, TOSTADO])).toEqual({ 2: 2 });
  });

  it('ignora los productos que ya no están en la lista', () => {
    const pedido = { items: [{ nombreProducto: 'Churros', cantidad: 1 }] };
    expect(cantidadesDePedido(pedido, [CAFE])).toEqual({});
  });
});

describe('sumarStockReservado', () => {
  it('devuelve al stock lo que el pedido ya tenía reservado', () => {
    const [cafe, tostado] = sumarStockReservado([CAFE, TOSTADO], { 1: 2 });
    expect(cafe.stock).toBe(5);
    expect(tostado.stock).toBe(10);
  });
});

describe('armarEntrega', () => {
  it('sin datos arranca en mostrador y con los campos vacíos', () => {
    expect(armarEntrega(undefined)).toEqual({ tipoDeEntrega: 'MOSTRADOR', nombreCliente: '', telefono: '' });
  });

  it('toma los datos de un pedido, cambiando null por texto vacío', () => {
    const pedido = { tipoDeEntrega: 'DELIVERY', nombreCliente: null, telefono: '1122' };
    expect(armarEntrega(pedido)).toEqual({ tipoDeEntrega: 'DELIVERY', nombreCliente: '', telefono: '1122' });
  });
});
