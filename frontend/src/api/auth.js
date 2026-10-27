import clienteHttp from './http.js';

export function iniciarSesion(email, password) {
  return clienteHttp.post('/auth/iniciar-sesion', { email, password });
}

export function registrarCliente(datos) {
  return clienteHttp.post('/auth/registro', datos);
}
