import { useState } from 'react';
import { actualizarMiPerfil } from '../api/clientes.js';
import useFormulario from '../hooks/useFormulario.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import Aviso from './Aviso.jsx';
import Campo from './Campo.jsx';
import { useNotificar } from './Notificaciones.jsx';

export default function FormularioPerfil({ perfil }) {
  const notificar = useNotificar();
  const { valores, cambiar } = useFormulario({
    nombre: perfil.nombre ?? '',
    telefono: perfil.telefono ?? '',
    direccion: perfil.direccion ?? '',
  });
  const [error, setError] = useState('');
  const [guardando, setGuardando] = useState(false);

  async function enviar(evento) {
    evento.preventDefault();
    setGuardando(true);
    setError('');
    try {
      await actualizarMiPerfil(valores);
      notificar('Perfil actualizado.');
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario perfil" onSubmit={enviar}>
      <Campo etiqueta="Email">
        <input value={perfil.email} disabled />
      </Campo>
      <Campo etiqueta="Nombre y apellido">
        <input required value={valores.nombre} onChange={cambiar('nombre')} />
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
      <button type="submit" className="btn btn-primario" disabled={guardando}>
        {guardando ? 'Guardando…' : 'Guardar cambios'}
      </button>
    </form>
  );
}
