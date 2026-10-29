const HTTP_PROHIBIDO = 403;
const HTTP_BAD_GATEWAY = 502;
const HTTP_NO_DISPONIBLE = 503;
const HTTP_SIN_RESPUESTA = 504;
const HTTP_SIN_SERVIDOR = [HTTP_BAD_GATEWAY, HTTP_NO_DISPONIBLE, HTTP_SIN_RESPUESTA];

const MENSAJE_INESPERADO = 'Ocurrió un error inesperado.';
const MENSAJE_SIN_SERVIDOR = 'No se pudo conectar con el servidor. Revisá que el backend esté corriendo.';

export const MENSAJE_SESION_VENCIDA = 'Tu sesión venció. Ingresá de nuevo.';

export function esErrorDePermiso(error) {
  return error?.response?.status === HTTP_PROHIBIDO;
}

// Sin respuesta, o el proxy avisa que el backend no contesta.
function noHayServidor(error) {
  const respuesta = error?.response;
  if (respuesta) {
    return HTTP_SIN_SERVIDOR.includes(respuesta.status);
  }
  return Boolean(error?.request);
}

export function obtenerMensajeDeError(error) {
  if (noHayServidor(error)) {
    return MENSAJE_SIN_SERVIDOR;
  }
  const respuesta = error?.response;
  if (respuesta && typeof respuesta.data === 'string' && respuesta.data.trim()) {
    return respuesta.data;
  }
  if (esErrorDePermiso(error)) {
    return 'No tenés permiso para hacer esto. Si cambiaste de usuario, cerrá sesión y volvé a ingresar.';
  }
  if (respuesta) {
    return MENSAJE_INESPERADO;
  }
  return error?.message || MENSAJE_INESPERADO;
}
