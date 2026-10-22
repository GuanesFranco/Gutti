import { TIPO_ENTREGA } from './dominio.js';

export const LARGO_MINIMO_PASSWORD = 8;

export function validarProducto(producto) {
  if (!producto.nombre) {
    return 'El nombre es obligatorio.';
  }
  if (Number.isNaN(producto.precio) || producto.precio <= 0) {
    return 'El precio debe ser mayor a 0.';
  }
  if (!Number.isInteger(producto.stock) || producto.stock < 1) {
    return 'El stock debe ser un número entero mayor a 0.';
  }
  return '';
}

export function validarEntrega(entrega) {
  if (entrega.tipoDeEntrega === TIPO_ENTREGA.DELIVERY && !entrega.telefono.trim()) {
    return 'Para delivery hace falta un teléfono de contacto.';
  }
  return '';
}

export function validarPassword(password) {
  if (password.length < LARGO_MINIMO_PASSWORD) {
    return `La contraseña debe tener al menos ${LARGO_MINIMO_PASSWORD} caracteres.`;
  }
  return '';
}
