import { quitarVacios } from '../utils/filtros.js';
import clienteHttp from './client.js';

export function listarProductos(filtros = {}) {
  return clienteHttp.get('/productos/consultarproductos', { params: quitarVacios(filtros) });
}

export function listarProductosActivos() {
  return listarProductos({ estadoActivo: true });
}

export function crearProducto(datos) {
  return clienteHttp.post('/productos/crearProducto', datos);
}

export function modificarProducto(id, datos) {
  return clienteHttp.put(`/productos/modificarProducto/${id}`, datos);
}

export function darDeBajaProducto(id) {
  return clienteHttp.delete(`/productos/eliminarProducto/${id}`);
}

export function activarProducto(id) {
  return clienteHttp.patch(`/productos/activarProducto/${id}`);
}
