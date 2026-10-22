// @vitest-environment jsdom
import { beforeEach, describe, expect, it } from 'vitest';
import clienteHttp from '../src/api/client.js';
import { guardarToken, obtenerToken } from '../src/api/token.js';
import { MENSAJE_SESION_VENCIDA } from '../src/utils/errores.js';
import { crearToken, segundosDesdeAhora } from './ayudantes.js';

const SEGUNDOS_POR_HORA = 3600;

function devolverAutorizacion(configuracion) {
  return Promise.resolve({
    data: configuracion.headers.Authorization ?? null,
    status: 200,
    headers: {},
    config: configuracion,
  });
}

describe('interceptor del cliente HTTP', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('agrega el token a cada llamada', async () => {
    const token = crearToken({ exp: segundosDesdeAhora(SEGUNDOS_POR_HORA) });
    guardarToken(token);
    await expect(clienteHttp.get('/x', { adapter: devolverAutorizacion })).resolves.toBe(`Bearer ${token}`);
  });

  it('no manda la llamada si el token venció y borra la sesión', async () => {
    guardarToken(crearToken({ exp: segundosDesdeAhora(-SEGUNDOS_POR_HORA) }));
    await expect(clienteHttp.get('/x', { adapter: devolverAutorizacion })).rejects.toThrow(MENSAJE_SESION_VENCIDA);
    expect(obtenerToken()).toBeNull();
  });

  it('sin sesión llama al backend sin token', async () => {
    await expect(clienteHttp.get('/x', { adapter: devolverAutorizacion })).resolves.toBeNull();
  });
});
