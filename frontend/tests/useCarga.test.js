// @vitest-environment jsdom
import { act, renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import useCarga from '../src/hooks/useCarga.js';

describe('useCarga', () => {
  it('devuelve los datos que trae la función de carga', async () => {
    const cargar = () => Promise.resolve(['café']);
    const { result } = renderHook(() => useCarga(cargar));
    await waitFor(() => expect(result.current.datos).toEqual(['café']));
    expect(result.current.error).toBe('');
  });

  it('muestra el mensaje del backend si la carga falla', async () => {
    const cargar = () => Promise.reject({ response: { status: 400, data: 'Fechas inválidas' } });
    const { result } = renderHook(() => useCarga(cargar));
    await waitFor(() => expect(result.current.error).toBe('Fechas inválidas'));
    expect(result.current.datos).toBeNull();
  });

  it('vuelve a cargar cuando se llama a recargar()', async () => {
    const cargar = vi.fn(() => Promise.resolve([]));
    const { result } = renderHook(() => useCarga(cargar));
    await waitFor(() => expect(result.current.datos).toEqual([]));
    const llamadasAntes = cargar.mock.calls.length;
    act(() => result.current.recargar());
    await waitFor(() => expect(cargar.mock.calls.length).toBe(llamadasAntes + 1));
  });
});
