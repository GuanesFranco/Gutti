import { useNotificar } from '../components/Notificaciones.jsx';
import { obtenerMensajeDeError } from '../utils/errores.js';

export default function useAccionConAviso() {
  const notificar = useNotificar();
  return async (accion) => {
    try {
      await accion();
    } catch (excepcion) {
      notificar(obtenerMensajeDeError(excepcion), true);
    }
  };
}
