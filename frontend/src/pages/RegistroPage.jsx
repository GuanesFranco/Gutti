import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registrarCliente } from '../api/auth.js';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import PantallaAcceso from '../components/PantallaAcceso.jsx';
import useFormulario from '../hooks/useFormulario.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { RUTAS } from '../utils/rutas.js';
import { LARGO_MINIMO_PASSWORD, validarPassword } from '../utils/validaciones.js';

export default function RegistroPage() {
  const navigate = useNavigate();
  const { valores, cambiar } = useFormulario({ nombre: '', email: '', password: '', telefono: '', direccion: '' });
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function enviar(evento) {
    evento.preventDefault();
    const errorPassword = validarPassword(valores.password);
    setError(errorPassword);
    if (errorPassword) {
      return;
    }
    setEnviando(true);
    try {
      await registrarCliente({ ...valores, email: valores.email.trim() });
      navigate(RUTAS.LOGIN, { state: { registrado: true } });
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      setEnviando(false);
    }
  }

  return (
    <PantallaAcceso titulo="Crear una cuenta">
      <form className="formulario" onSubmit={enviar}>
        <Campo etiqueta="Nombre y apellido">
          <input required value={valores.nombre} onChange={cambiar('nombre')} />
        </Campo>
        <Campo etiqueta="Email">
          <input type="email" required value={valores.email} onChange={cambiar('email')} />
        </Campo>
        <Campo etiqueta="Contraseña" ayuda={`Mínimo ${LARGO_MINIMO_PASSWORD} caracteres.`}>
          <input type="password" required value={valores.password} onChange={cambiar('password')} />
        </Campo>
        <div className="formulario-fila">
          <Campo etiqueta="Teléfono">
            <input type="tel" required value={valores.telefono} onChange={cambiar('telefono')} />
          </Campo>
          <Campo etiqueta="Dirección">
            <input required value={valores.direccion} onChange={cambiar('direccion')} />
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
