import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext.jsx';
import { ROL } from './utils/dominio.js';
import RutaProtegida from './auth/RutaProtegida.jsx';
import RutaSinSesion from './auth/RutaSinSesion.jsx';
import Layout from './components/Layout.jsx';
import CatalogoPage from './pages/CatalogoPage.jsx';
import ClientesPage from './pages/ClientesPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import PedidoPage from './pages/PedidoPage.jsx';
import PerfilPage from './pages/PerfilPage.jsx';
import PedidosPage from './pages/PedidosPage.jsx';
import ProductosAdminPage from './pages/ProductosAdminPage.jsx';
import RegistroPage from './pages/RegistroPage.jsx';
import { RUTAS } from './utils/rutas.js';

function Inicio() {
  const { estaAutenticado, rol } = useAuth();
  if (!estaAutenticado) {
    return <Navigate to={RUTAS.CATALOGO} replace />;
  }
  return <Navigate to={rol === ROL.ADMIN ? RUTAS.PEDIDOS : RUTAS.NUEVO_PEDIDO} replace />;
}

export default function App() {
  return (
    <Routes>
      <Route
        path={RUTAS.LOGIN}
        element={
          <RutaSinSesion>
            <LoginPage />
          </RutaSinSesion>
        }
      />
      <Route
        path={RUTAS.REGISTRO}
        element={
          <RutaSinSesion>
            <RegistroPage />
          </RutaSinSesion>
        }
      />
      <Route element={<Layout />}>
        <Route path={RUTAS.INICIO} element={<Inicio />} />
        <Route path={RUTAS.CATALOGO} element={<CatalogoPage />} />
        <Route
          path={RUTAS.PEDIDOS}
          element={
            <RutaProtegida rolRequerido={ROL.ADMIN}>
              <PedidosPage />
            </RutaProtegida>
          }
        />
        <Route
          path={RUTAS.PRODUCTOS}
          element={
            <RutaProtegida rolRequerido={ROL.ADMIN}>
              <ProductosAdminPage />
            </RutaProtegida>
          }
        />
        <Route
          path={RUTAS.NUEVO_PEDIDO}
          element={
            <RutaProtegida>
              <PedidoPage />
            </RutaProtegida>
          }
        />
        <Route
          path={RUTAS.CLIENTES}
          element={
            <RutaProtegida rolRequerido={ROL.ADMIN}>
              <ClientesPage />
            </RutaProtegida>
          }
        />
        <Route
          path={RUTAS.PERFIL}
          element={
            <RutaProtegida rolRequerido={ROL.CLIENTE}>
              <PerfilPage />
            </RutaProtegida>
          }
        />
        <Route
          path={RUTAS.EDITAR_PEDIDO}
          element={
            <RutaProtegida rolRequerido={ROL.ADMIN}>
              <PedidoPage />
            </RutaProtegida>
          }
        />
      </Route>
      <Route path="*" element={<Navigate to={RUTAS.INICIO} replace />} />
    </Routes>
  );
}
