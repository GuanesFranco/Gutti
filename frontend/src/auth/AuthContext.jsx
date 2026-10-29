import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { iniciarSesion } from '../api/auth.js';
import { registrarAlVencerSesion } from '../api/http.js';
import { MENSAJE_SESION_VENCIDA } from '../utils/errores.js';
import {
  borrarSesion,
  estaVencido,
  guardarRol,
  guardarToken,
  obtenerEmail,
  obtenerRol,
  obtenerToken,
} from '../api/token.js';
import { detectarRol } from './rol.js';

const AuthContext = createContext(null);

function leerSesionGuardada() {
  const token = obtenerToken();
  const rol = obtenerRol();
  if (!token || !rol || estaVencido(token)) {
    borrarSesion();
    return null;
  }
  return { token, rol };
}

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(leerSesionGuardada);
  const [aviso, setAviso] = useState('');

  const cerrarSesion = useCallback((mensaje = '') => {
    borrarSesion();
    setSesion(null);
    setAviso(mensaje);
  }, []);

  useEffect(() => {
    registrarAlVencerSesion(() => cerrarSesion(MENSAJE_SESION_VENCIDA));
  }, [cerrarSesion]);

  async function ingresar(email, password) {
    const respuesta = await iniciarSesion(email, password);
    const token = respuesta.token;
    guardarToken(token);
    try {
      const rol = respuesta.rol || (await detectarRol(token));
      guardarRol(rol);
      setSesion({ token, rol });
      setAviso('');
    } catch (error) {
      borrarSesion();
      throw error;
    }
  }

  const actualizarTokenSesion = useCallback((nuevoToken) => {
    guardarToken(nuevoToken);
    setSesion((anterior) => (anterior ? { ...anterior, token: nuevoToken } : anterior));
  }, []);

  const valor = {
    estaAutenticado: Boolean(sesion),
    rol: sesion?.rol,
    email: sesion ? obtenerEmail(sesion.token) : null,
    aviso,
    ingresar,
    cerrarSesion,
    actualizarTokenSesion,
  };

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
