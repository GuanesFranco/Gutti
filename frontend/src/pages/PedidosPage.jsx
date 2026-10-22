import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { cambiarEstadoPedido, eliminarPedido, listarPedidos, obtenerRecaudacion } from '../api/pedidos.js';
import Aviso from '../components/Aviso.jsx';
import DialogoConfirmacion from '../components/DialogoConfirmacion.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import Segmentos from '../components/Segmentos.jsx';
import TarjetaPedido from '../components/TarjetaPedido.jsx';
import useCarga from '../hooks/useCarga.js';
import useFormulario from '../hooks/useFormulario.js';
import { ESTADO_PEDIDO, TIPO_ENTREGA } from '../utils/dominio.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { formatearPesos, obtenerFechaDeHoy } from '../utils/formato.js';
import { RUTAS } from '../utils/rutas.js';

const OPCIONES_ESTADO = [
  { valor: ESTADO_PEDIDO.PENDIENTE, texto: 'Pendientes' },
  { valor: ESTADO_PEDIDO.ENTREGADO, texto: 'Entregados' },
  { valor: '', texto: 'Todos' },
];

export default function PedidosPage() {
  const notificar = useNotificar();
  const { valores: filtros, cambiar: cambiarFiltro } = useFormulario({
    desde: obtenerFechaDeHoy(),
    hasta: obtenerFechaDeHoy(),
    tipoDeEntrega: '',
  });
  const [estado, setEstado] = useState(ESTADO_PEDIDO.PENDIENTE);
  const [aEliminar, setAEliminar] = useState(null);
  const [idOcupado, setIdOcupado] = useState(null);

  const cargarPedidos = useCallback(async () => {
    const [pedidos, recaudacion] = await Promise.all([listarPedidos(filtros), obtenerRecaudacion(filtros)]);
    return { pedidos, recaudacion };
  }, [filtros]);
  const { datos, error, recargar } = useCarga(cargarPedidos);

  const pedidos = datos?.pedidos || [];
  const pendientes = pedidos.filter((pedido) => pedido.estado === ESTADO_PEDIDO.PENDIENTE).length;
  const visibles = pedidos.filter((pedido) => !estado || pedido.estado === estado);

  async function marcarEntregado(pedido) {
    setIdOcupado(pedido.id);
    try {
      await cambiarEstadoPedido(pedido.id, ESTADO_PEDIDO.ENTREGADO);
      notificar(`Pedido #${pedido.id} entregado.`);
      recargar();
    } catch (excepcion) {
      notificar(obtenerMensajeDeError(excepcion), true);
    } finally {
      setIdOcupado(null);
    }
  }

  async function eliminar() {
    await eliminarPedido(aEliminar.id);
    notificar(`Pedido #${aEliminar.id} eliminado.`);
    recargar();
  }

  return (
    <>
      <div className="pagina-cabecera">
        <h1>Pedidos</h1>
        <Link to={RUTAS.NUEVO_PEDIDO} className="btn btn-primario">
          Nuevo pedido
        </Link>
      </div>

      <section className="resumen">
        <div className="resumen-caja">
          <span className="resumen-etiqueta">Vendido en el período</span>
          <span className="resumen-numero">{datos ? formatearPesos(datos.recaudacion) : '—'}</span>
        </div>
        <div className="resumen-dato">
          <span className="resumen-numero-chico">{datos ? pendientes : '—'}</span>
          <span className="resumen-etiqueta">por preparar</span>
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
        <Segmentos opciones={OPCIONES_ESTADO} valor={estado} alCambiar={setEstado} etiqueta="Estado" />
      </div>

      <Aviso error={error} alReintentar={recargar} />
      {datos && visibles.length === 0 && <p className="estado-vacio">No hay pedidos para mostrar.</p>}
      <div className="tickets">
        {visibles.map((pedido) => (
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
