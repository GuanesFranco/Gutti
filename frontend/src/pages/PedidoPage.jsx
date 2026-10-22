import { useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { obtenerPedido } from '../api/pedidos.js';
import { obtenerMiPerfil } from '../api/clientes.js';
import { listarProductosActivos } from '../api/productos.js';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import FormularioPedido from '../components/FormularioPedido.jsx';
import useCarga from '../hooks/useCarga.js';
import { ROL } from '../utils/dominio.js';
import { armarEntrega, cantidadesDePedido, sumarStockReservado } from '../utils/pedido.js';

const AVISO_PRODUCTO_FALTANTE =
  'Algún producto de este pedido ya no está activo: revisá las cantidades antes de guardar.';

export default function PedidoPage() {
  const { id } = useParams();
  const esCliente = useAuth().rol === ROL.CLIENTE;
  const cargar = useCallback(async () => {
    const [productos, pedido, perfil] = await Promise.all([
      listarProductosActivos(),
      id ? obtenerPedido(id) : null,
      esCliente && !id ? obtenerMiPerfil().catch(() => null) : null,
    ]);
    return { productos, pedido, perfil };
  }, [id, esCliente]);
  const { datos, error, recargar } = useCarga(cargar);

  const cantidadesIniciales = datos?.pedido ? cantidadesDePedido(datos.pedido, datos.productos) : {};
  const datosPerfil = datos?.perfil && { nombreCliente: datos.perfil.nombre, telefono: datos.perfil.telefono };
  const faltanProductos = datos?.pedido && Object.keys(cantidadesIniciales).length < datos.pedido.items.length;
  const tituloNuevo = esCliente ? 'Hacer un pedido' : 'Nuevo pedido';
  const titulo = id ? `Editar pedido #${id}` : tituloNuevo;

  return (
    <>
      <div className="pagina-cabecera">
        <h1>{titulo}</h1>
      </div>
      <Aviso error={error} alReintentar={recargar} />
      {faltanProductos && <Aviso error={AVISO_PRODUCTO_FALTANTE} />}
      {datos && (
        <FormularioPedido
          key={id}
          productos={sumarStockReservado(datos.productos, cantidadesIniciales)}
          pedido={datos.pedido}
          entregaInicial={armarEntrega(datos.pedido || datosPerfil)}
          cantidadesIniciales={cantidadesIniciales}
          alRecargar={recargar}
        />
      )}
    </>
  );
}
