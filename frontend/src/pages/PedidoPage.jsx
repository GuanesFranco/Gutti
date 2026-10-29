import { useCallback, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { obtenerMiPerfil } from '../api/clientes.js';
import { crearPedido, modificarPedido, obtenerPedido } from '../api/pedidos.js';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import FormularioPedido from '../components/FormularioPedido.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import PedidoConfirmado from '../components/PedidoConfirmado.jsx';
import useCarga from '../hooks/useCarga.js';
import useProductosActivos from '../hooks/useProductosActivos.js';
import { ROL } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import { prepararFormulario } from '../utils/pedido.js';
import { RUTAS } from '../utils/rutas.js';

const AVISO_PRODUCTO_FALTANTE = 'Algún producto de este pedido ya no está activo: revisá el pedido antes de guardar.';

function elegirTitulo(id, esAdmin) {
  if (id) {
    return `Editar pedido #${id}`;
  }
  return esAdmin ? 'Nuevo pedido' : 'Hacer un pedido';
}

function elegirSubtitulo(id, esAdmin) {
  if (id) {
    return 'Modificá las cantidades o el tipo de entrega del pedido';
  }
  return esAdmin
    ? 'Carga de pedido en mostrador o comanda de delivery'
    : 'Elegí tus delicias favoritas y te las preparamos al instante';
}

function elegirTextoConfirmar(id, estaAutenticado) {
  if (id) {
    return 'Guardar cambios';
  }
  return estaAutenticado ? 'Confirmar pedido' : 'Ingresar para confirmar';
}

export default function PedidoPage() {
  const { id } = useParams();
  const { estaAutenticado, rol } = useAuth();
  const esAdmin = rol === ROL.ADMIN;
  const esCliente = estaAutenticado && !esAdmin;
  const ubicacion = useLocation();
  const [borrador, setBorrador] = useState(ubicacion.state?.borrador);
  const navigate = useNavigate();
  const notificar = useNotificar();
  const catalogo = useProductosActivos();
  const [confirmado, setConfirmado] = useState(null);
  const [numeroDeFormulario, setNumeroDeFormulario] = useState(0);
  const cargarDatos = useCallback(async () => {
    const [pedido, perfil] = await Promise.all([
      id ? obtenerPedido(id) : null,
      esCliente ? obtenerMiPerfil().catch(() => null) : null,
    ]);
    return { pedido, perfil };
  }, [id, esCliente]);
  const { datos, error, recargar } = useCarga(cargarDatos);

  // Sin cuenta se puede armar el pedido; para confirmarlo hay que ingresar.
  async function guardar(pedido, armado) {
    if (!estaAutenticado) {
      navigate(RUTAS.LOGIN, { state: { volverA: { ruta: RUTAS.NUEVO_PEDIDO, estado: { borrador: armado } } } });
      return;
    }
    const guardado = id ? await modificarPedido(id, pedido) : await crearPedido(pedido);
    if (!esAdmin) {
      setConfirmado(guardado);
      return;
    }
    notificar(`Pedido #${guardado.id} ${id ? 'actualizado' : 'registrado'} por ${formatearPesos(guardado.total)}.`);
    navigate(RUTAS.PEDIDOS);
  }

  function hacerOtroPedido() {
    navigate(RUTAS.NUEVO_PEDIDO, { replace: true });
    setBorrador(null);
    setConfirmado(null);
    setNumeroDeFormulario((numero) => numero + 1);
    catalogo.recargar();
  }

  if (confirmado) {
    return <PedidoConfirmado pedido={confirmado} alHacerOtro={hacerOtroPedido} />;
  }

  const listo = datos && catalogo.productos;
  const inicial = listo && prepararFormulario({ ...datos, borrador, productos: catalogo.productos });

  return (
    <>
      <Encabezado
        titulo={elegirTitulo(id, esAdmin)}
        subtitulo={elegirSubtitulo(id, esAdmin)}
      />
      <Aviso error={error} alReintentar={recargar} />
      {!catalogo.productos && <Aviso error={catalogo.error} alReintentar={catalogo.recargar} />}
      {listo && (
        <FormularioPedido
          key={`${id}-${numeroDeFormulario}`}
          catalogo={catalogo}
          esCliente={!esAdmin}
          textoConfirmar={elegirTextoConfirmar(id, estaAutenticado)}
          entregaInicial={inicial.entrega}
          carritoInicial={inicial.carrito}
          avisoInicial={inicial.faltanProductos ? AVISO_PRODUCTO_FALTANTE : ''}
          alGuardar={guardar}
        />
      )}
    </>
  );
}
