import axios from 'axios';
import { MENSAJE_SESION_VENCIDA } from '../utils/errores.js';
import { borrarSesion, estaVencido, obtenerToken } from './token.js';

const URL_BASE = import.meta.env.VITE_API_URL || '/api/v1';
const TIMEOUT_MILISEGUNDOS = 10000;

const clienteHttp = axios.create({
  baseURL: URL_BASE,
  timeout: TIMEOUT_MILISEGUNDOS,
});

let alVencerSesion = () => {};

export function registrarAlVencerSesion(funcion) {
  alVencerSesion = funcion;
}

// Si el token ya venció, la llamada no sale y se cierra la sesión.
clienteHttp.interceptors.request.use((configuracion) => {
  const token = obtenerToken();
  if (token && estaVencido(token)) {
    borrarSesion();
    alVencerSesion();
    return Promise.reject(new Error(MENSAJE_SESION_VENCIDA));
  }
  if (token) {
    configuracion.headers.Authorization = `Bearer ${token}`;
  }
  return configuracion;
});

const HTTP_NO_AUTORIZADO = 401;

// Desempaqueta la respuesta y captura 401 Unauthorized para limpiar sesión de forma segura
clienteHttp.interceptors.response.use(
  (respuesta) => respuesta.data,
  (error) => {
    if (error.response?.status === HTTP_NO_AUTORIZADO) {
      borrarSesion();
      alVencerSesion();
    }
    return Promise.reject(error);
  }
);

export default clienteHttp;
