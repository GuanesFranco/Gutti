import { useCallback, useEffect, useState } from 'react';
import { obtenerMensajeDeError } from '../utils/errores.js';

// Trae datos del backend y los vuelve a pedir cuando cambian los filtros.
export default function useCarga(cargar) {
  const [resultado, setResultado] = useState({ datos: null, error: '' });
  const [version, setVersion] = useState(0);

  useEffect(() => {
    let sigueVigente = true;
    cargar()
      .then((datos) => {
        if (sigueVigente) {
          setResultado({ datos, error: '' });
        }
      })
      .catch((error) => {
        if (sigueVigente) {
          setResultado({ datos: null, error: obtenerMensajeDeError(error) });
        }
      });
    return () => {
      sigueVigente = false;
    };
  }, [cargar, version]);

  const recargar = useCallback(() => setVersion((numero) => numero + 1), []);

  return { ...resultado, recargar };
}
