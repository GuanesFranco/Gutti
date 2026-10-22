const HTTP_PROHIBIDO = 403;

export const MENSAJE_SESION_VENCIDA = 'Tu sesión venció. Ingresá de nuevo.';

export function esErrorDePermiso(error) {
  return error?.response?.status === HTTP_PROHIBIDO;
}

export function obtenerMensajeDeError(error) {
  const respuesta = error?.response;
  if (respuesta && typeof respuesta.data === 'string' && respuesta.data.trim()) {
    return respuesta.data;
  }
  if (esErrorDePermiso(error)) {
    return 'No tenés permiso para hacer esto. Si cambiaste de usuario, cerrá sesión y volvé a ingresar.';
  }
  if (respuesta) {
    return 'Ocurrió un error inesperado.';
  }
  if (error?.request) {
    return 'No se pudo conectar con el servidor. Revisá que el backend esté corriendo.';
  }
  return error?.message || 'Ocurrió un error inesperado.';
}
