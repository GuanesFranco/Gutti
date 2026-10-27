import { describe, expect, it } from 'vitest';
import { nombrarCategoria } from '../src/utils/dominio.js';
import { formatearPesos, obtenerFechaDeHoy } from '../src/utils/formato.js';

describe('formatearPesos', () => {
  it('usa el formato argentino sin decimales cuando el monto es entero', () => {
    expect(formatearPesos(2500)).toMatch(/2\.500$/);
  });

  it('muestra los centavos cuando los hay', () => {
    expect(formatearPesos(2499.5)).toMatch(/2\.499,5$/);
  });

  it('trata un valor vacío como cero', () => {
    expect(formatearPesos(undefined)).toMatch(/0$/);
  });
});

describe('obtenerFechaDeHoy', () => {
  it('devuelve la fecha en formato yyyy-MM-dd', () => {
    expect(obtenerFechaDeHoy(new Date(2026, 5, 10, 12, 0))).toBe('2026-06-10');
  });

  it('usa la fecha local aunque en UTC ya sea el día siguiente', () => {
    expect(obtenerFechaDeHoy(new Date(2026, 5, 9, 23, 30))).toBe('2026-06-09');
  });
});

describe('nombrarCategoria', () => {
  it('traduce la categoría del backend a su nombre para mostrar', () => {
    expect(nombrarCategoria('SANGUCHES')).toBe('Sánguches');
  });

  it('muestra tal cual una categoría que no conoce', () => {
    expect(nombrarCategoria('BEBIDAS')).toBe('BEBIDAS');
  });
});
