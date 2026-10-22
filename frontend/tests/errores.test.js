import { describe, expect, it } from 'vitest';
import { esErrorDePermiso, obtenerMensajeDeError } from '../src/utils/errores.js';

describe('obtenerMensajeDeError', () => {
  it('muestra el texto que manda el backend', () => {
    const error = { response: { status: 409, data: 'No hay stock suficiente' } };
    expect(obtenerMensajeDeError(error)).toBe('No hay stock suficiente');
  });

  it('explica qué hacer ante un 403 sin texto', () => {
    expect(obtenerMensajeDeError({ response: { status: 403, data: '' } })).toMatch(/permiso/);
  });

  it('avisa que el servidor no responde cuando no hubo respuesta', () => {
    expect(obtenerMensajeDeError({ request: {} })).toMatch(/servidor/);
  });

  it('usa el mensaje propio de un error que no vino del servidor', () => {
    expect(obtenerMensajeDeError(new Error('Tu sesión venció.'))).toBe('Tu sesión venció.');
  });
});

describe('esErrorDePermiso', () => {
  it('reconoce solo el 403', () => {
    expect(esErrorDePermiso({ response: { status: 403 } })).toBe(true);
    expect(esErrorDePermiso({ response: { status: 401 } })).toBe(false);
  });
});
