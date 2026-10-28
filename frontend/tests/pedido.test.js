import { describe, expect, it } from 'vitest';
import {
  armarEntrega,
  calcularTotal,
  cambiarCantidad,
  carritoDePedido,
  itemsDelCarrito,
  lineasDelCarrito,
  prepararFormulario,
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

describe('calcularTotal', () => {
  it('suma precio por cantidad para mostrar el total mientras se arma el pedido', () => {
    const carrito = { 1: { producto: CAFE, cantidad: 2 }, 2: { producto: TOSTADO, cantidad: 1 } };
    expect(calcularTotal(carrito)).toBe(9500);
    expect(calcularTotal({})).toBe(0);
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
    expect(armarEntrega(undefined)).toEqual({
      tipoDeEntrega: 'MOSTRADOR',
      nombreCliente: '',
      telefono: '',
      direccion: '',
    });
  });

  it('toma los datos de un pedido, cambiando null por texto vacío', () => {
    const pedido = { tipoDeEntrega: 'DELIVERY', nombreCliente: null, telefono: '1122' };
    expect(armarEntrega(pedido)).toEqual({
      tipoDeEntrega: 'DELIVERY',
      nombreCliente: '',
      telefono: '1122',
      direccion: '',
    });
  });
});

describe('prepararFormulario', () => {
  it('con un pedido armado sin cuenta, lo retoma tal cual', () => {
    const borrador = { entrega: armarEntrega(undefined), carrito: { 1: { producto: CAFE, cantidad: 2 } } };
    expect(prepararFormulario({ borrador, productos: [CAFE] })).toEqual({ ...borrador, faltanProductos: false });
  });

  it('al editar, avisa si algún producto del pedido ya no está activo', () => {
    const pedido = { tipoDeEntrega: 'MOSTRADOR', items: [{ nombreProducto: 'Churros', cantidad: 1 }] };
    expect(prepararFormulario({ pedido, productos: [CAFE] }).faltanProductos).toBe(true);
  });

  it('en un pedido nuevo, toma el nombre y el teléfono del perfil', () => {
    const perfil = { nombre: 'Ana', telefono: '1122', direccion: 'Calle 123' };
    expect(prepararFormulario({ perfil, productos: [CAFE] }).entrega).toEqual({
      tipoDeEntrega: 'MOSTRADOR',
      nombreCliente: 'Ana',
      telefono: '1122',
      direccion: 'Calle 123',
    });
  });

  it('con un borrador que solo tiene carrito, inicializa la entrega por defecto de forma segura', () => {
    const borrador = { carrito: { 1: { producto: CAFE, cantidad: 2 } } };
    const resultado = prepararFormulario({ borrador, productos: [CAFE] });
    expect(resultado.carrito).toEqual(borrador.carrito);
    expect(resultado.entrega.tipoDeEntrega).toBe('MOSTRADOR');
    expect(resultado.entrega.nombreCliente).toBe('');
  });
});
