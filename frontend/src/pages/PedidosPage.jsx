import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  cambiarEstadoPedido,
  eliminarPedido,
  listarPedidos,
  obtenerPedido,
  obtenerRecaudacion,
} from '../api/pedidos.js';
import Aviso from '../components/Aviso.jsx';
import DialogoConfirmacion from '../components/DialogoConfirmacion.jsx';
import Encabezado from '../components/Encabezado.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import TarjetaPedido from '../components/TarjetaPedido.jsx';
import useAccionConAviso from '../hooks/useAccionConAviso.js';
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
  const [numero, setNumero] = useState('');
  const [buscado, setBuscado] = useState(null);

  const cargarPedidos = useCallback(async () => {
    const [pedidos, recaudacion] = await Promise.all([listarPedidos(filtros), obtenerRecaudacion(filtros)]);
    return { pedidos, recaudacion };
  }, [filtros]);
  const { datos, error, recargar } = useCarga(cargarPedidos);

  const pedidos = buscado ? [buscado] : datos?.pedidos || [];

  function buscar(evento) {
    evento.preventDefault();
    conAviso(async () => setBuscado(await obtenerPedido(numero)));
  }

  function verTodos() {
    setBuscado(null);
    setNumero('');
    recargar();
  }

  async function marcarEntregado(pedido) {
    setIdOcupado(pedido.id);
    await conAviso(async () => {
      await cambiarEstadoPedido(pedido.id, ESTADO_PEDIDO.ENTREGADO);
      notificar(`Pedido #${pedido.id} entregado.`);
      verTodos();
    });
    setIdOcupado(null);
  }

  async function eliminar() {
    await eliminarPedido(aEliminar.id);
    notificar(`Pedido #${aEliminar.id} eliminado.`);
    verTodos();
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
          <span className="resumen-etiqueta">Vendido en el período, pendientes incluidos</span>
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
      <form className="filtros" onSubmit={buscar}>
        <input
          type="number"
          min="1"
          aria-label="Número de pedido"
          placeholder="N° de pedido"
          value={numero}
          onChange={(evento) => setNumero(evento.target.value)}
        />
        <button type="submit" className="btn btn-ghost" disabled={!numero}>
          Buscar pedido
        </button>
        {buscado && (
          <button type="button" className="btn btn-ghost" onClick={verTodos}>
            Ver todos
          </button>
        )}
      </form>

      <Aviso error={error} alReintentar={recargar} />
      {datos && pedidos.length === 0 && <p className="estado-vacio">No hay pedidos para mostrar.</p>}
      <div className="tickets">
        {pedidos.map((pedido) => (
          <TarjetaPedido
            key={pedido.id}
            pedido={pedido}
            ocupado={idOcupado === pedido.id}
            alMarcarEntregado={marcarEntregado}
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
