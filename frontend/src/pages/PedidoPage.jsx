import { useCallback, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
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
import { armarEntrega, carritoDePedido } from '../utils/pedido.js';
import { RUTAS } from '../utils/rutas.js';

const AVISO_PRODUCTO_FALTANTE = 'Algún producto de este pedido ya no está activo: revisá el pedido antes de guardar.';

export default function PedidoPage() {
  const { id } = useParams();
  const esCliente = useAuth().rol === ROL.CLIENTE;
  const navigate = useNavigate();
  const notificar = useNotificar();
  const catalogo = useProductosActivos();
  const [confirmado, setConfirmado] = useState(null);
  const [vuelta, setVuelta] = useState(0);
  const cargarDatos = useCallback(async () => {
    // Si el cliente todavía no tiene perfil, el pedido arranca con los datos de entrega vacíos.
    const [pedido, perfil] = await Promise.all([
      id ? obtenerPedido(id) : null,
      esCliente ? obtenerMiPerfil().catch(() => null) : null,
    ]);
    return { pedido, perfil };
  }, [id, esCliente]);
  const { datos, error, recargar } = useCarga(cargarDatos);

  async function guardar(pedido) {
    const guardado = id ? await modificarPedido(id, pedido) : await crearPedido(pedido);
    if (esCliente) {
      setConfirmado(guardado);
      return;
    }
    notificar(`Pedido #${guardado.id} ${id ? 'actualizado' : 'registrado'} por ${formatearPesos(guardado.total)}.`);
    navigate(RUTAS.PEDIDOS);
  }

  function hacerOtroPedido() {
    setConfirmado(null);
    setVuelta((numero) => numero + 1);
    catalogo.recargar();
  }

  if (confirmado) {
    return <PedidoConfirmado pedido={confirmado} alHacerOtro={hacerOtroPedido} />;
  }

  const listo = datos && catalogo.productos;
  const carritoInicial = listo && datos.pedido ? carritoDePedido(datos.pedido, catalogo.productos) : {};
  const faltanProductos = listo && datos.pedido && Object.keys(carritoInicial).length < datos.pedido.items.length;
  const datosPerfil = datos?.perfil && { nombreCliente: datos.perfil.nombre, telefono: datos.perfil.telefono };
  const tituloNuevo = esCliente ? 'Hacer un pedido' : 'Nuevo pedido';

  return (
    <>
      <Encabezado titulo={id ? `Editar pedido #${id}` : tituloNuevo} />
      <Aviso error={error} alReintentar={recargar} />
      {!catalogo.productos && <Aviso error={catalogo.error} alReintentar={catalogo.recargar} />}
      {listo && (
        <FormularioPedido
          key={`${id}-${vuelta}`}
          catalogo={catalogo}
          esCliente={esCliente}
          editando={Boolean(id)}
          entregaInicial={armarEntrega(datos.pedido || datosPerfil)}
          carritoInicial={carritoInicial}
          avisoInicial={faltanProductos ? AVISO_PRODUCTO_FALTANTE : ''}
          alGuardar={guardar}
        />
      )}
    </>
  );
}
