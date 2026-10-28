import { useAuth } from '../auth/AuthContext.jsx';
import { actualizarMiPerfil, obtenerMiPerfil } from '../api/clientes.js';
import Aviso from '../components/Aviso.jsx';
import Campo from '../components/Campo.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import useCarga from '../hooks/useCarga.js';
import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';

function FormularioPerfil({ perfil, alGuardar }) {
  const { valores, cambiar } = useFormulario({
    email: perfil.email ?? '',
    nombre: perfil.nombre ?? '',
    telefono: perfil.telefono ?? '',
    direccion: perfil.direccion ?? '',
  });
  const { enviar, enviando, error } = useEnvio();

  async function guardar(evento) {
    evento.preventDefault();
    await enviar(() => alGuardar(valores));
  }

  return (
    <form onSubmit={guardar} className="perfil-card-limpio">
      <div className="perfil-bloque">
        <h2 className="perfil-bloque-titulo">Datos personales</h2>

        <Campo etiqueta="Email">
          <input
            type="email"
            placeholder="ejemplo@email.com"
            value={valores.email}
            onChange={cambiar('email')}
            required
          />
        </Campo>

        <Campo etiqueta="Nombre y apellido">
          <input
            placeholder="Ej. Juan Pérez"
            value={valores.nombre}
            onChange={cambiar('nombre')}
            required
          />
        </Campo>

        <Campo etiqueta="Teléfono de contacto">
          <input
            type="tel"
            placeholder="Ej. 11 2345-6789"
            value={valores.telefono}
            onChange={cambiar('telefono')}
          />
        </Campo>
      </div>

      <div className="perfil-bloque">
        <h2 className="perfil-bloque-titulo">Dirección habitual de delivery</h2>

        <Campo etiqueta="Dirección (calle, número, piso/depto)">
          <input
            placeholder="Ej. Av. Corrientes 1234, 4to B"
            value={valores.direccion}
            onChange={cambiar('direccion')}
          />
        </Campo>
      </div>

      <Aviso error={error} />

      <div className="perfil-pie-botonera">
        <button type="submit" className="btn btn-primario" disabled={enviando}>
          {enviando ? 'Guardando cambios…' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}

export default function PerfilPage() {
  const notificar = useNotificar();
  const { actualizarTokenSesion } = useAuth();
  const { datos: perfil, error, recargar } = useCarga(obtenerMiPerfil);

  async function guardar(datos) {
    const respuesta = await actualizarMiPerfil(datos);
    if (respuesta?.token) {
      actualizarTokenSesion(respuesta.token);
    }
    notificar('Perfil actualizado correctamente.');
    recargar();
  }

  return (
    <div className="perfil-contenedor-principal">
      <div className="pagina-cabecera perfil-cabecera-simple">
        <h1>Mi perfil</h1>
      </div>
      <Aviso error={error} alReintentar={recargar} />
      {perfil && <FormularioPerfil perfil={perfil} alGuardar={guardar} />}
    </div>
  );
}
