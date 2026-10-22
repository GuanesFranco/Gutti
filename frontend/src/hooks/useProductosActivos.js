import { useCallback, useState } from 'react';
import { listarProductosActivos } from '../api/productos.js';
import useCarga from './useCarga.js';

// Los filtros se los pide al backend; el cliente solo ve productos activos.
export default function useProductosActivos() {
  const [filtros, setFiltros] = useState({ nombre: '', categoria: '' });
  const cargar = useCallback(() => listarProductosActivos(filtros), [filtros]);
  const { datos, error, recargar } = useCarga(cargar);

  function cambiarFiltro(campo, valor) {
    setFiltros((anteriores) => ({ ...anteriores, [campo]: valor }));
  }

  return { productos: datos, error, recargar, filtros, cambiarFiltro };
}
