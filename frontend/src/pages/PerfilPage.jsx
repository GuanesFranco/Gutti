import { obtenerMiPerfil } from '../api/clientes.js';
import Aviso from '../components/Aviso.jsx';
import FormularioPerfil from '../components/FormularioPerfil.jsx';
import useCarga from '../hooks/useCarga.js';

export default function PerfilPage() {
  const { datos: perfil, error, recargar } = useCarga(obtenerMiPerfil);

  return (
    <>
      <div className="pagina-cabecera">
        <h1>Mi perfil</h1>
      </div>
      <Aviso error={error} alReintentar={recargar} />
      {perfil && <FormularioPerfil perfil={perfil} />}
    </>
  );
}
