import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import PantallaAcceso from '../components/PantallaAcceso.jsx';
import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';
import { RUTAS } from '../utils/rutas.js';

export default function LoginPage() {
  const { ingresar, aviso } = useAuth();
  const ubicacion = useLocation();
  const { valores, cambiar } = useFormulario({ email: '', password: '' });
  const [mostrarPassword, setMostrarPassword] = useState(false);
  const { enviar, enviando, error } = useEnvio();

  async function iniciar(evento) {
    evento.preventDefault();
    await enviar(() => ingresar(valores.email.trim(), valores.password));
  }

  return (
    <PantallaAcceso titulo="Ingresar">
      <Aviso ok={ubicacion.state?.registrado ? 'Cuenta creada. Ya podés ingresar.' : aviso} />
      <form className="formulario" onSubmit={iniciar}>
        <Campo etiqueta="Email">
          <input type="email" value={valores.email} onChange={cambiar('email')} />
        </Campo>
        <Campo etiqueta="Contraseña">
          <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
            <input
              type={mostrarPassword ? 'text' : 'password'}
              value={valores.password}
              onChange={cambiar('password')}
              style={{ width: '100%', paddingRight: '2.5rem' }}
            />
            <button
              type="button"
              onClick={() => setMostrarPassword((v) => !v)}
              style={{
                position: 'absolute',
                right: '0.75rem',
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                padding: '0.2rem',
                color: 'var(--caramelo)',
                fontSize: '0.8rem',
                fontWeight: 600,
              }}
              title={mostrarPassword ? 'Ocultar contraseña' : 'Ver contraseña'}
              aria-label={mostrarPassword ? 'Ocultar contraseña' : 'Ver contraseña'}
            >
              {mostrarPassword ? 'Ocultar' : 'Mostrar'}
            </button>
          </div>
        </Campo>
        <Aviso error={error} />
        <button type="submit" className="btn btn-primario btn-ancho" disabled={enviando}>
          {enviando ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>
      <p className="acceso-alternativa">
        ¿Primera vez?{' '}
        <Link to={RUTAS.REGISTRO} state={ubicacion.state}>
          Crear una cuenta
        </Link>{' '}
        · <Link to={RUTAS.CATALOGO}>Ver el catálogo</Link>
      </p>
    </PantallaAcceso>
  );
}
