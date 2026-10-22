// @vitest-environment jsdom
import { act, renderHook } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import useEnvio from '../src/hooks/useEnvio.js';

describe('useEnvio', () => {
  it('devuelve true y no deja error si el backend acepta', async () => {
    const { result } = renderHook(() => useEnvio());
    let resultado;
    await act(async () => {
      resultado = await result.current.enviar(() => Promise.resolve());
    });
    expect(resultado).toBe(true);
    expect(result.current.error).toBe('');
    expect(result.current.enviando).toBe(false);
  });

  it('devuelve false y guarda el mensaje del backend si rechaza', async () => {
    const { result } = renderHook(() => useEnvio());
    let resultado;
    await act(async () => {
      resultado = await result.current.enviar(() =>
        Promise.reject({ response: { status: 409, data: 'Stock insuficiente' } }),
      );
    });
    expect(resultado).toBe(false);
    expect(result.current.error).toBe('Stock insuficiente');
  });

  it('arranca con el aviso inicial que recibe', () => {
    const { result } = renderHook(() => useEnvio('Revisá el pedido'));
    expect(result.current.error).toBe('Revisá el pedido');
  });
});
