import { Navigate, useLocation } from 'react-router-dom';
import { RUTAS } from '../utils/rutas.js';
import { useAuth } from './AuthContext.jsx';

// Al ingresar vuelve a donde estaba, con el pedido que venía armando.
export default function RutaSinSesion({ children }) {
  const { estaAutenticado } = useAuth();
  const volverA = useLocation().state?.volverA;

  if (estaAutenticado) {
    return <Navigate to={volverA?.ruta || RUTAS.INICIO} state={volverA?.estado} replace />;
  }
  return children;
}
