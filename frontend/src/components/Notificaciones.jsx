import { createContext, useCallback, useContext, useRef, useState } from 'react';
import { obtenerMensajeDeError } from '../utils/errores.js';

const DURACION_MS = 4000;
const NotificacionesContext = createContext(() => {});

export function NotificacionesProvider({ children }) {
  const [notificaciones, setNotificaciones] = useState([]);
  const ultimoId = useRef(0);

  const notificar = useCallback((texto, esError = false) => {
    ultimoId.current += 1;
    const id = ultimoId.current;
    setNotificaciones((lista) => [...lista, { id, texto, esError }]);
    setTimeout(() => {
      setNotificaciones((lista) => lista.filter((notificacion) => notificacion.id !== id));
    }, DURACION_MS);
  }, []);

  return (
    <NotificacionesContext.Provider value={notificar}>
      {children}
      <div className="notificaciones" role="status">
        {notificaciones.map((notificacion) => (
          <div key={notificacion.id} className={`notificacion ${notificacion.esError ? 'notificacion-error' : ''}`}>
            {notificacion.texto}
          </div>
        ))}
      </div>
    </NotificacionesContext.Provider>
  );
}

export function useNotificar() {
  return useContext(NotificacionesContext);
}

export function useAccionConAviso() {
  const notificar = useNotificar();
  return async (accion) => {
    try {
      await accion();
    } catch (excepcion) {
      notificar(obtenerMensajeDeError(excepcion), true);
    }
  };
}
