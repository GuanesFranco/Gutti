import { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react';

const DURACION_MS = 4000;
const NotificacionesContext = createContext(() => {});

export function NotificacionesProvider({ children }) {
  const [notificaciones, setNotificaciones] = useState([]);
  const ultimoId = useRef(0);
  const temporizadores = useRef([]);

  useEffect(() => () => temporizadores.current.forEach(clearTimeout), []);

  const notificar = useCallback((texto, esError = false) => {
    ultimoId.current += 1;
    const id = ultimoId.current;
    setNotificaciones((lista) => [...lista, { id, texto, esError }]);
    const temporizador = setTimeout(() => {
      setNotificaciones((lista) => lista.filter((notificacion) => notificacion.id !== id));
      temporizadores.current = temporizadores.current.filter((pendiente) => pendiente !== temporizador);
    }, DURACION_MS);
    temporizadores.current.push(temporizador);
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
