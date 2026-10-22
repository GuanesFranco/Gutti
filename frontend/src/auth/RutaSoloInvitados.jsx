import { Navigate } from 'react-router-dom';
import { RUTAS } from '../utils/rutas.js';
import { useAuth } from './AuthContext.jsx';

export default function RutaSoloInvitados({ children }) {
  const { estaAutenticado } = useAuth();

  if (estaAutenticado) {
    return <Navigate to={RUTAS.INICIO} replace />;
  }
  return children;
}
