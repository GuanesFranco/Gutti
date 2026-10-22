import { quitarVacios } from '../utils/filtros.js';
import clienteHttp from './client.js';

export function listarPedidos(filtros = {}) {
  return clienteHttp.get('/pedidos', { params: quitarVacios(filtros) });
}

export function obtenerRecaudacion(filtros = {}) {
  return clienteHttp.get('/pedidos/recaudacion', { params: quitarVacios(filtros) });
}

export function obtenerPedido(id) {
  return clienteHttp.get(`/pedidos/${id}`);
}

export function crearPedido(datos) {
  return clienteHttp.post('/pedidos', datos);
}

export function modificarPedido(id, datos) {
  return clienteHttp.put(`/pedidos/${id}`, datos);
}

export function cambiarEstadoPedido(id, estado) {
  return clienteHttp.patch(`/pedidos/${id}/estado`, null, { params: { estado } });
}

export function eliminarPedido(id) {
  return clienteHttp.delete(`/pedidos/${id}`);
}
