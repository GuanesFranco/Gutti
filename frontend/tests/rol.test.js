import { beforeEach, describe, expect, it, vi } from 'vitest';
import { obtenerRecaudacion } from '../src/api/pedidos.js';
import { detectarRol } from '../src/auth/rol.js';
import { ROL } from '../src/utils/dominio.js';

vi.mock('../src/api/pedidos.js', () => ({ obtenerRecaudacion: vi.fn() }));

describe('detectarRol', () => {
  beforeEach(() => {
    vi.mocked(obtenerRecaudacion).mockReset();
  });

  it('es ADMIN si el backend deja ver la recaudación', async () => {
    vi.mocked(obtenerRecaudacion).mockResolvedValue(0);
    await expect(detectarRol()).resolves.toBe(ROL.ADMIN);
  });

  it('es CLIENTE si el backend responde 403', async () => {
    vi.mocked(obtenerRecaudacion).mockRejectedValue({ response: { status: 403 } });
    await expect(detectarRol()).resolves.toBe(ROL.CLIENTE);
  });

  it('deja pasar cualquier otro error (por ejemplo, el servidor caído)', async () => {
    const sinConexion = { request: {} };
    vi.mocked(obtenerRecaudacion).mockRejectedValue(sinConexion);
    await expect(detectarRol()).rejects.toBe(sinConexion);
  });
});
