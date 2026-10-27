const CLAVE_TOKEN = 'gutti_token';
const CLAVE_ROL = 'gutti_rol';
const MILISEGUNDOS_POR_SEGUNDO = 1000;

// La sesión dura lo que la pestaña: al cerrarla se pierde el token.
export function guardarToken(token) {
  sessionStorage.setItem(CLAVE_TOKEN, token);
}

export function guardarRol(rol) {
  sessionStorage.setItem(CLAVE_ROL, rol);
}

export function obtenerToken() {
  return sessionStorage.getItem(CLAVE_TOKEN);
}

export function obtenerRol() {
  return sessionStorage.getItem(CLAVE_ROL);
}

export function borrarSesion() {
  sessionStorage.removeItem(CLAVE_TOKEN);
  sessionStorage.removeItem(CLAVE_ROL);
}

// El JWT trae los datos en la parte del medio, en base64.
function leerToken(token) {
  try {
    const datos = token.split('.')[1];
    const base64 = datos.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
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
