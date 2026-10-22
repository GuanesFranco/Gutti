import { describe, expect, it } from 'vitest';
import { quitarVacios } from '../src/utils/filtros.js';

describe('quitarVacios', () => {
  it('saca los filtros sin valor y deja los demás', () => {
    expect(quitarVacios({ desde: '2026-10-20', hasta: '', tipo: null, activo: false })).toEqual({
      desde: '2026-10-20',
      activo: false,
    });
  });
});
