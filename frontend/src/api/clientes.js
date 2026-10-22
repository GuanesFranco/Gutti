import clienteHttp from './client.js';

export function obtenerMiPerfil() {
  return clienteHttp.get('/clientes/mi-perfil');
}

export function actualizarMiPerfil(datos) {
  return clienteHttp.put('/clientes/mi-perfil', datos);
}

export function listarClientes() {
  return clienteHttp.get('/clientes');
}

export function obtenerCliente(id) {
  return clienteHttp.get(`/clientes/${id}`);
}

export function actualizarCliente(id, datos) {
  return clienteHttp.put(`/clientes/${id}`, datos);
}
