import { actualizarMiPerfil, obtenerMiPerfil } from '../api/clientes.js';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import FormularioCliente from '../components/FormularioCliente.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import useCarga from '../hooks/useCarga.js';

export default function PerfilPage() {
  const notificar = useNotificar();
  const { datos: perfil, error, recargar } = useCarga(obtenerMiPerfil);

  async function guardar(datos) {
    await actualizarMiPerfil(datos);
    notificar('Perfil actualizado.');
  }

  return (
    <>
      <Encabezado titulo="Mi perfil" />
      <Aviso error={error} alReintentar={recargar} />
      {perfil && <FormularioCliente cliente={perfil} alGuardar={guardar} />}
    </>
  );
}
