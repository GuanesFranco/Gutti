import clienteHttp from './client.js';

export function obtenerMiPerfil() {
  return clienteHttp.get('/clientes/mi-perfil');
}

export function actualizarMiPerfil(datos) {
  return clienteHttp.put('/clientes/mi-perfil', datos);
}
