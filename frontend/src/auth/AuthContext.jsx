import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { iniciarSesion } from '../api/auth.js';
import { registrarAlVencerSesion } from '../api/client.js';
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
    const { token } = await iniciarSesion(email, password);
    guardarToken(token);
    try {
      const rol = await detectarRol();
      guardarRol(rol);
      setSesion({ token, rol });
      setAviso('');
    } catch (error) {
      borrarSesion();
      throw error;
    }
  }

  const valor = {
    estaAutenticado: Boolean(sesion),
    rol: sesion?.rol,
    email: sesion ? obtenerEmail(sesion.token) : null,
    aviso,
    ingresar,
    cerrarSesion,
  };

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
