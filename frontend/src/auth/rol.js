import { obtenerRecaudacion } from '../api/pedidos.js';
import { obtenerRolDeToken } from '../api/token.js';
import { ROL } from '../utils/dominio.js';
import { esErrorDePermiso } from '../utils/errores.js';

// Si el token trae el claim 'rol', lo usamos directo sin llamadas de red.
// Si no, recurre al chequeo de permisos sobre recaudación.
export async function detectarRol(token) {
  if (token) {
    const rolEnToken = obtenerRolDeToken(token);
    if (rolEnToken) {
      return rolEnToken.toUpperCase() === 'ADMIN' ? ROL.ADMIN : ROL.CLIENTE;
    }
  }

  try {
    await obtenerRecaudacion();
    return ROL.ADMIN;
  } catch (error) {
    if (esErrorDePermiso(error)) {
      return ROL.CLIENTE;
    }
    throw error;
  }
}
