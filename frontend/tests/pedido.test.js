import { describe, expect, it } from 'vitest';
import {
  armarEntrega,
  cambiarCantidad,
  carritoDePedido,
  itemsDelCarrito,
  lineasDelCarrito,
} from '../src/utils/pedido.js';

const CAFE = { id: 1, nombre: 'Café', precio: 2500, stock: 3 };
const TOSTADO = { id: 2, nombre: 'Tostado', precio: 4500, stock: 10 };

describe('cambiarCantidad', () => {
  it('suma un producto nuevo y vuelve a sumar el mismo', () => {
    const carrito = cambiarCantidad(cambiarCantidad({}, CAFE, 1), CAFE, 1);
    expect(carrito).toEqual({ 1: { producto: CAFE, cantidad: 2 } });
  });

  it('saca el producto cuando la cantidad llega a cero', () => {
    const carrito = cambiarCantidad({ 1: { producto: CAFE, cantidad: 1 } }, CAFE, -1);
    expect(carrito).toEqual({});
  });

  it('no limita por stock: eso lo valida el backend', () => {
    const carrito = cambiarCantidad({ 1: { producto: CAFE, cantidad: 3 } }, CAFE, 1);
    expect(carrito[1].cantidad).toBe(4);
  });

  it('no modifica el carrito original', () => {
    const original = { 1: { producto: CAFE, cantidad: 1 } };
    cambiarCantidad(original, TOSTADO, 1);
    expect(original).toEqual({ 1: { producto: CAFE, cantidad: 1 } });
  });
});

describe('itemsDelCarrito', () => {
  it('arma los items que pide el backend', () => {
    const carrito = { 1: { producto: CAFE, cantidad: 2 }, 2: { producto: TOSTADO, cantidad: 1 } };
    expect(itemsDelCarrito(carrito)).toEqual([
      { productoId: 1, cantidad: 2 },
      { productoId: 2, cantidad: 1 },
    ]);
    expect(lineasDelCarrito(carrito)).toHaveLength(2);
  });
});

describe('carritoDePedido', () => {
  it('vuelve a armar el carrito buscando cada producto por nombre', () => {
    const pedido = { items: [{ nombreProducto: 'Tostado', cantidad: 2 }] };
    expect(carritoDePedido(pedido, [CAFE, TOSTADO])).toEqual({ 2: { producto: TOSTADO, cantidad: 2 } });
  });

  it('ignora los productos que ya no están en la lista', () => {
    const pedido = { items: [{ nombreProducto: 'Churros', cantidad: 1 }] };
    expect(carritoDePedido(pedido, [CAFE])).toEqual({});
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
