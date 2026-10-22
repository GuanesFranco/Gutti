const CLAVE_TOKEN = 'gutti_token';
const CLAVE_ROL = 'gutti_rol';
const MILISEGUNDOS_POR_SEGUNDO = 1000;

export function guardarToken(token) {
  localStorage.setItem(CLAVE_TOKEN, token);
}

export function guardarRol(rol) {
  localStorage.setItem(CLAVE_ROL, rol);
}

export function obtenerToken() {
  return localStorage.getItem(CLAVE_TOKEN);
}

export function obtenerRol() {
  return localStorage.getItem(CLAVE_ROL);
}

export function borrarSesion() {
  localStorage.removeItem(CLAVE_TOKEN);
  localStorage.removeItem(CLAVE_ROL);
}

function leerToken(token) {
  try {
    return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return null;
  }
}

export function obtenerEmail(token) {
  return leerToken(token)?.sub ?? null;
}

export function estaVencido(token) {
  const contenido = leerToken(token);
  if (!contenido) {
    return true;
  }
  const vencimiento = contenido.exp * MILISEGUNDOS_POR_SEGUNDO;
  return Date.now() >= vencimiento;
}
