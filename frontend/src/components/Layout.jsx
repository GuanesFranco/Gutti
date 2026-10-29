import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
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
    { ruta: RUTAS.CATALOGO, texto: 'Menú' },
    { ruta: RUTAS.NUEVO_PEDIDO, texto: 'Hacer un pedido' },
    { ruta: RUTAS.MIS_PEDIDOS, texto: 'Mis pedidos' },
    { ruta: RUTAS.PERFIL, texto: 'Mi perfil' },
  ],
};

const ENLACES_SIN_SESION = [
  { ruta: RUTAS.CATALOGO, texto: 'Menú' },
  { ruta: RUTAS.NUEVO_PEDIDO, texto: 'Hacer un pedido' },
];

function formatearNombreUsuario(email, rol) {
  if (rol === ROL.ADMIN) return 'Admin';
  if (!email) return 'Mi cuenta';
  const prefix = email.split('@')[0].replace(/[._-]/g, ' ');
  return prefix.charAt(0).toUpperCase() + prefix.slice(1);
}

export default function Layout() {
  const { estaAutenticado, rol, email, cerrarSesion } = useAuth();
  const navigate = useNavigate();
  const enlaces = ENLACES[rol] || ENLACES_SIN_SESION;

  function salir() {
    cerrarSesion();
    navigate(RUTAS.LOGIN);
  }

  return (
    <div className="layout-starbucks">
      <header className="cabecera-principal">
        <div className="cabecera-contenedor">
          <Link to={RUTAS.CATALOGO} className="cabecera-marca">
            <img className="cabecera-logo" src={logo} alt="Gutti" />
            <div className="cabecera-marca-texto">
              <span className="cabecera-marca-nombre">GUTTI</span>
              <span className="cabecera-marca-lema">CHURRERÍA & PASTELERÍA</span>
            </div>
          </Link>

          <nav className="cabecera-nav" aria-label="Secciones">
            {enlaces.map((enlace) => (
              <NavLink key={enlace.ruta} to={enlace.ruta} end className="cabecera-enlace nav-enlace">
                {enlace.texto}
              </NavLink>
            ))}
          </nav>

          <div className="cabecera-acciones">
            {estaAutenticado ? (
              <div className="cabecera-usuario">
                <span className="usuario-chip" title={email}>
                  <svg
                    className="icono-usuario"
                    viewBox="0 0 24 24"
                    width="16"
                    height="16"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                  >
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                  <span>{formatearNombreUsuario(email, rol)}</span>
                  {rol === ROL.ADMIN && <span className="chip chip-admin">Admin</span>}
                </span>
                <button type="button" className="btn btn-ghost btn-chico" onClick={salir}>
                  Cerrar sesión
                </button>
              </div>
            ) : (
              <NavLink to={RUTAS.LOGIN} className="btn btn-primario btn-chico">
                Ingresar
              </NavLink>
            )}
          </div>
        </div>
      </header>

      <main className="contenido contenido-principal">
        <Outlet />
      </main>
    </div>
  );
}
