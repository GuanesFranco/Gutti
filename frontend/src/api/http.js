import axios from 'axios';
import { MENSAJE_SESION_VENCIDA } from '../utils/errores.js';
import { borrarSesion, estaVencido, obtenerToken } from './token.js';

const URL_BASE = '/api/v1';

const clienteHttp = axios.create({ baseURL: URL_BASE });

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

clienteHttp.interceptors.response.use((respuesta) => respuesta.data);

export default clienteHttp;
