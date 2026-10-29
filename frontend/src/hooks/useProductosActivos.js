import { useCallback, useEffect, useState } from 'react';
import { listarProductosActivos } from '../api/productos.js';
import useCarga from './useCarga.js';

const DEMORA_BUSQUEDA_MS = 250;

export default function useProductosActivos() {
  const [filtros, setFiltros] = useState({ nombre: '', categoria: '' });
  const [nombreDebounced, setNombreDebounced] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setNombreDebounced(filtros.nombre);
    }, DEMORA_BUSQUEDA_MS);
    return () => clearTimeout(timer);
  }, [filtros.nombre]);

  const cargar = useCallback(
    () => listarProductosActivos({ nombre: nombreDebounced, categoria: filtros.categoria }),
    [nombreDebounced, filtros.categoria]
  );
  const { datos, error, recargar } = useCarga(cargar);

  function cambiarFiltro(campo, valor) {
    setFiltros((anteriores) => ({ ...anteriores, [campo]: valor }));
  }

  return { productos: datos, error, recargar, filtros, cambiarFiltro };
}
