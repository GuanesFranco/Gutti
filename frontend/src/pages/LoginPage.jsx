import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import PantallaAcceso from '../components/PantallaAcceso.jsx';
import useFormulario from '../hooks/useFormulario.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { RUTAS } from '../utils/rutas.js';

export default function LoginPage() {
  const { ingresar, aviso } = useAuth();
  const navigate = useNavigate();
  const ubicacion = useLocation();
  const { valores, cambiar } = useFormulario({ email: '', password: '' });
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function enviar(evento) {
    evento.preventDefault();
    setError('');
    setEnviando(true);
    try {
      await ingresar(valores.email.trim(), valores.password);
      navigate(RUTAS.INICIO, { replace: true });
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      setEnviando(false);
    }
  }

  return (
    <PantallaAcceso titulo="Ingresar">
      <Aviso ok={ubicacion.state?.registrado ? 'Cuenta creada. Ya podés ingresar.' : aviso} />
      <form className="formulario" onSubmit={enviar}>
        <Campo etiqueta="Email">
          <input type="email" required value={valores.email} onChange={cambiar('email')} />
        </Campo>
        <Campo etiqueta="Contraseña">
          <input type="password" required value={valores.password} onChange={cambiar('password')} />
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
