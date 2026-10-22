import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import PantallaAcceso from '../components/PantallaAcceso.jsx';
import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';
import { RUTAS } from '../utils/rutas.js';

export default function LoginPage() {
  const { ingresar, aviso } = useAuth();
  const navigate = useNavigate();
  const ubicacion = useLocation();
  const { valores, cambiar } = useFormulario({ email: '', password: '' });
  const { enviar, enviando, error } = useEnvio();

  async function iniciar(evento) {
    evento.preventDefault();
    if (await enviar(() => ingresar(valores.email.trim(), valores.password))) {
      navigate(RUTAS.INICIO, { replace: true });
    }
  }

  return (
    <PantallaAcceso titulo="Ingresar">
      <Aviso ok={ubicacion.state?.registrado ? 'Cuenta creada. Ya podés ingresar.' : aviso} />
      <form className="formulario" onSubmit={iniciar}>
        <Campo etiqueta="Email">
          <input type="email" value={valores.email} onChange={cambiar('email')} />
        </Campo>
        <Campo etiqueta="Contraseña">
          <input type="password" value={valores.password} onChange={cambiar('password')} />
        </Campo>
        <Aviso error={error} />
        <button type="submit" className="btn btn-primario btn-ancho" disabled={enviando}>
          {enviando ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>
      <p className="acceso-alternativa">
        ¿Primera vez? <Link to={RUTAS.REGISTRO}>Crear una cuenta</Link> ·{' '}
        <Link to={RUTAS.CATALOGO}>Ver el catálogo</Link>
      </p>
    </PantallaAcceso>
  );
}
