import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { cambiarEstadoPedido, eliminarPedido, listarPedidos, obtenerRecaudacion } from '../api/pedidos.js';
import Aviso from '../components/Aviso.jsx';
import DialogoConfirmacion from '../components/DialogoConfirmacion.jsx';
import Encabezado from '../components/Encabezado.jsx';
import { useAccionConAviso, useNotificar } from '../components/Notificaciones.jsx';
import TarjetaPedido from '../components/TarjetaPedido.jsx';
import useCarga from '../hooks/useCarga.js';
import useFormulario from '../hooks/useFormulario.js';
import { ESTADO_PEDIDO, TIPO_ENTREGA } from '../utils/dominio.js';
import { formatearPesos, obtenerFechaDeHoy } from '../utils/formato.js';
import { RUTAS } from '../utils/rutas.js';

export default function PedidosPage() {
  const notificar = useNotificar();
  const conAviso = useAccionConAviso();
  const { valores: filtros, cambiar: cambiarFiltro } = useFormulario({
    desde: obtenerFechaDeHoy(),
    hasta: obtenerFechaDeHoy(),
    tipoDeEntrega: '',
  });
  const [aEliminar, setAEliminar] = useState(null);
  const [idOcupado, setIdOcupado] = useState(null);

  const cargarPedidos = useCallback(async () => {
    const [pedidos, recaudacion] = await Promise.all([listarPedidos(filtros), obtenerRecaudacion(filtros)]);
    return { pedidos, recaudacion };
  }, [filtros]);
  const { datos, error, recargar } = useCarga(cargarPedidos);

  const pedidos = datos?.pedidos || [];

  async function cambiarEstado(pedido, estado) {
    setIdOcupado(pedido.id);
    await conAviso(async () => {
      await cambiarEstadoPedido(pedido.id, estado);
      notificar(`Pedido #${pedido.id}: ${estado === ESTADO_PEDIDO.ENTREGADO ? 'entregado' : 'pendiente'}.`);
      recargar();
    });
    setIdOcupado(null);
  }

  async function eliminar() {
    await eliminarPedido(aEliminar.id);
    notificar(`Pedido #${aEliminar.id} eliminado.`);
    recargar();
  }

  return (
    <>
      <Encabezado titulo="Pedidos">
        <Link to={RUTAS.NUEVO_PEDIDO} className="btn btn-primario">
          Nuevo pedido
        </Link>
      </Encabezado>

      <section className="resumen">
        <div className="resumen-caja">
          <span className="resumen-etiqueta">Vendido en el período</span>
          <span className="resumen-numero">{datos ? formatearPesos(datos.recaudacion) : '—'}</span>
        </div>
      </section>

      <div className="filtros">
        <input type="date" aria-label="Desde" value={filtros.desde} onChange={cambiarFiltro('desde')} />
        <input type="date" aria-label="Hasta" value={filtros.hasta} onChange={cambiarFiltro('hasta')} />
        <select aria-label="Tipo de entrega" value={filtros.tipoDeEntrega} onChange={cambiarFiltro('tipoDeEntrega')}>
          <option value="">Mostrador y delivery</option>
          <option value={TIPO_ENTREGA.MOSTRADOR}>Solo mostrador</option>
          <option value={TIPO_ENTREGA.DELIVERY}>Solo delivery</option>
        </select>
      </div>

      <Aviso error={error} alReintentar={recargar} />
      {datos && pedidos.length === 0 && <p className="estado-vacio">No hay pedidos para mostrar.</p>}
      <div className="tickets">
        {pedidos.map((pedido) => (
          <TarjetaPedido
            key={pedido.id}
            pedido={pedido}
            ocupado={idOcupado === pedido.id}
            alCambiarEstado={cambiarEstado}
            alEliminar={setAEliminar}
          />
        ))}
      </div>

      {aEliminar && (
        <DialogoConfirmacion
          titulo={`Eliminar pedido #${aEliminar.id}`}
          texto="El pedido se borra y el stock de sus productos se devuelve."
          textoAccion="Eliminar pedido"
          alConfirmar={eliminar}
          alCerrar={() => setAEliminar(null)}
        />
      )}
    </>
  );
}
