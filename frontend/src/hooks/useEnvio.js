import { useState } from 'react';
import { obtenerMensajeDeError } from '../utils/errores.js';

export default function useEnvio(errorInicial = '') {
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState(errorInicial);

  async function enviar(accion) {
    setEnviando(true);
    setError('');
    try {
      await accion();
      return true;
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      return false;
    } finally {
      setEnviando(false);
    }
  }

  return { enviar, enviando, error, setError };
}
