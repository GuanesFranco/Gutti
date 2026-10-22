import { describe, expect, it } from 'vitest';
import { TIPO_ENTREGA } from '../src/utils/dominio.js';
import { validarEntrega, validarPassword, validarProducto } from '../src/utils/validaciones.js';

describe('validarProducto', () => {
  const productoValido = { nombre: 'Café', precio: 2500, stock: 10 };

  it('acepta un producto con nombre, precio y stock válidos', () => {
    expect(validarProducto(productoValido)).toBe('');
  });

  it('pide el nombre', () => {
    expect(validarProducto({ ...productoValido, nombre: '' })).toBe('El nombre es obligatorio.');
  });

  it('rechaza un precio igual a cero', () => {
    expect(validarProducto({ ...productoValido, precio: 0 })).toBe('El precio debe ser mayor a 0.');
  });

  it('rechaza un stock con decimales o menor a uno', () => {
    expect(validarProducto({ ...productoValido, stock: 1.5 })).not.toBe('');
    expect(validarProducto({ ...productoValido, stock: 0 })).not.toBe('');
  });
});

describe('validarEntrega', () => {
  it('pide teléfono para delivery', () => {
    expect(validarEntrega({ tipoDeEntrega: TIPO_ENTREGA.DELIVERY, telefono: '  ' })).not.toBe('');
  });

  it('no pide teléfono para mostrador', () => {
    expect(validarEntrega({ tipoDeEntrega: TIPO_ENTREGA.MOSTRADOR, telefono: '' })).toBe('');
  });
});

describe('validarPassword', () => {
  it('rechaza contraseñas de menos de 8 caracteres', () => {
    expect(validarPassword('corta')).not.toBe('');
  });

  it('acepta contraseñas de 8 caracteres o más', () => {
    expect(validarPassword('12345678')).toBe('');
  });
});
