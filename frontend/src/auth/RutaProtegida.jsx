import { Navigate } from 'react-router-dom';
import { RUTAS } from '../utils/rutas.js';
import { useAuth } from './AuthContext.jsx';

export default function RutaProtegida({ children, rolRequerido }) {
  const { estaAutenticado, rol } = useAuth();

  if (!estaAutenticado) {
    return <Navigate to={RUTAS.LOGIN} replace />;
  }
  if (rolRequerido && rol !== rolRequerido) {
    return <Navigate to={RUTAS.INICIO} replace />;
  }
  return children;
}
