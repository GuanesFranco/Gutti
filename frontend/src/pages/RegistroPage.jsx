import { Link, useNavigate } from 'react-router-dom';
import { registrarCliente } from '../api/auth.js';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import PantallaAcceso from '../components/PantallaAcceso.jsx';
import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';
import { RUTAS } from '../utils/rutas.js';

export default function RegistroPage() {
  const navigate = useNavigate();
  const { valores, cambiar } = useFormulario({ nombre: '', email: '', password: '', telefono: '', direccion: '' });
  const { enviar, enviando, error } = useEnvio();

  async function registrar(evento) {
    evento.preventDefault();
    if (await enviar(() => registrarCliente({ ...valores, email: valores.email.trim() }))) {
      navigate(RUTAS.LOGIN, { state: { registrado: true } });
    }
  }

  return (
    <PantallaAcceso titulo="Crear una cuenta">
      <form className="formulario" onSubmit={registrar}>
        <Campo etiqueta="Nombre y apellido">
          <input value={valores.nombre} onChange={cambiar('nombre')} />
        </Campo>
        <Campo etiqueta="Email">
          <input type="email" value={valores.email} onChange={cambiar('email')} />
        </Campo>
        <Campo etiqueta="Contraseña">
          <input type="password" value={valores.password} onChange={cambiar('password')} />
        </Campo>
        <div className="formulario-fila">
          <Campo etiqueta="Teléfono">
            <input type="tel" value={valores.telefono} onChange={cambiar('telefono')} />
          </Campo>
          <Campo etiqueta="Dirección">
            <input value={valores.direccion} onChange={cambiar('direccion')} />
          </Campo>
        </div>
        <Aviso error={error} />
        <button type="submit" className="btn btn-primario btn-ancho" disabled={enviando}>
          {enviando ? 'Creando cuenta…' : 'Crear cuenta'}
        </button>
      </form>
      <p className="acceso-alternativa">
        ¿Ya tenés cuenta? <Link to={RUTAS.LOGIN}>Ingresar</Link>
      </p>
    </PantallaAcceso>
  );
}
