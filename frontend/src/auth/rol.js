import { obtenerRecaudacion } from '../api/pedidos.js';
import { ROL } from '../utils/dominio.js';
import { esErrorDePermiso } from '../utils/errores.js';

// El token no trae el rol: si el usuario puede ver la recaudación es admin, si no es cliente.
export async function detectarRol() {
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
