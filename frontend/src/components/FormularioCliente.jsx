import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';
import Aviso from './Aviso.jsx';
import Campo from './Campo.jsx';

export default function FormularioCliente({ cliente, alGuardar, children }) {
  const { valores, cambiar } = useFormulario({
    nombre: cliente.nombre ?? '',
    telefono: cliente.telefono ?? '',
    direccion: cliente.direccion ?? '',
  });
  const { enviar, enviando, error } = useEnvio();

  async function guardar(evento) {
    evento.preventDefault();
    await enviar(() => alGuardar(valores));
  }

  return (
    <form className="formulario perfil" onSubmit={guardar}>
      <Campo etiqueta="Email">
        <input value={cliente.email ?? ''} disabled />
      </Campo>
      <Campo etiqueta="Nombre y apellido">
        <input value={valores.nombre} onChange={cambiar('nombre')} />
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
      <div className="botonera">
        {children}
        <button type="submit" className="btn btn-primario" disabled={enviando}>
          {enviando ? 'Guardando…' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}
