import { describe, expect, it } from 'vitest';
import { estaVencido, obtenerEmail } from '../src/api/token.js';
import { crearToken, segundosDesdeAhora } from './ayudantes.js';

const SEGUNDOS_POR_HORA = 3600;

describe('token', () => {
  it('lee el email del token', () => {
    expect(obtenerEmail(crearToken({ sub: 'admin@gutti.com' }))).toBe('admin@gutti.com');
  });

  it('detecta un token vencido', () => {
    expect(estaVencido(crearToken({ exp: segundosDesdeAhora(-SEGUNDOS_POR_HORA) }))).toBe(true);
  });

  it('acepta un token que todavía no venció', () => {
    expect(estaVencido(crearToken({ exp: segundosDesdeAhora(SEGUNDOS_POR_HORA) }))).toBe(false);
  });

  it('considera vencido un token mal formado', () => {
    expect(estaVencido('no-es-un-token')).toBe(true);
  });
});
