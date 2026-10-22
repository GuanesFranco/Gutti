import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import logo from '../assets/logo.jpg';
import { useAuth } from '../auth/AuthContext.jsx';
import { ROL } from '../utils/dominio.js';
import { RUTAS } from '../utils/rutas.js';

const ENLACES = {
  [ROL.ADMIN]: [
    { ruta: RUTAS.PEDIDOS, texto: 'Pedidos' },
    { ruta: RUTAS.NUEVO_PEDIDO, texto: 'Nuevo pedido' },
    { ruta: RUTAS.PRODUCTOS, texto: 'Productos' },
    { ruta: RUTAS.CLIENTES, texto: 'Clientes' },
    { ruta: RUTAS.CATALOGO, texto: 'Catálogo' },
  ],
  [ROL.CLIENTE]: [
    { ruta: RUTAS.NUEVO_PEDIDO, texto: 'Hacer un pedido' },
    { ruta: RUTAS.PERFIL, texto: 'Mi perfil' },
    { ruta: RUTAS.CATALOGO, texto: 'Catálogo' },
  ],
};

const ENLACES_SIN_SESION = [{ ruta: RUTAS.CATALOGO, texto: 'Catálogo' }];

export default function Layout() {
  const { estaAutenticado, rol, email, cerrarSesion } = useAuth();
  const navigate = useNavigate();
  const enlaces = ENLACES[rol] || ENLACES_SIN_SESION;

  function salir() {
    cerrarSesion();
    navigate(RUTAS.LOGIN);
  }

  return (
    <div className="marco">
      <aside className="barra">
        <div className="marca">
          <img className="marca-logo" src={logo} alt="" />
          <span className="marca-nombre">Gutti</span>
        </div>
        <nav className="nav" aria-label="Secciones">
          {enlaces.map((enlace) => (
            <NavLink key={enlace.ruta} to={enlace.ruta} end className="nav-enlace">
              {enlace.texto}
            </NavLink>
          ))}
        </nav>
        <div className="barra-pie">
          {estaAutenticado ? (
            <>
              <span className="barra-usuario">{email}</span>
              <button type="button" className="btn btn-ghost btn-chico" onClick={salir}>
                Cerrar sesión
              </button>
            </>
          ) : (
            <NavLink to={RUTAS.LOGIN} className="btn btn-primario btn-chico">
              Ingresar
            </NavLink>
          )}
        </div>
      </aside>
      <main className="contenido">
        <Outlet />
      </main>
    </div>
  );
}
