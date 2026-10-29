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

const TAMANO_PAGINA = 10;

export default function PedidosPage() {
  const notificar = useNotificar();
  const conAviso = useAccionConAviso();
  const { valores: filtros, cambiar: cambiarFiltro } = useFormulario({
    desde: obtenerFechaDeHoy(),
    hasta: obtenerFechaDeHoy(),
    tipoDeEntrega: '',
  });
  const [pagina, setPagina] = useState(0);
  const [aEliminar, setAEliminar] = useState(null);
  const [idOcupado, setIdOcupado] = useState(null);
  const [numero, setNumero] = useState('');
  const [buscado, setBuscado] = useState(null);
  const [pestanaEstado, setPestanaEstado] = useState('TODOS');

  const cargarPedidos = useCallback(async () => {
    const [respuestaPedidos, recaudacion] = await Promise.all([
      listarPedidos({ ...filtros, page: pagina, size: TAMANO_PAGINA }),
      obtenerRecaudacion(filtros),
    ]);
    const lista = respuestaPedidos?.content || (Array.isArray(respuestaPedidos) ? respuestaPedidos : []);
    const totalPaginas = respuestaPedidos?.totalPages || 1;
    const totalElementos = respuestaPedidos?.totalElements ?? lista.length;
    return { pedidos: lista, totalPaginas, totalElementos, recaudacion };
  }, [filtros, pagina]);
  const { datos, error, recargar } = useCarga(cargarPedidos);

  const pedidos = buscado ? [buscado] : datos?.pedidos || [];
  const cantidadPendientes = pedidos.filter((p) => p.estado === ESTADO_PEDIDO.PENDIENTE).length;
  const pedidosVisibles = pedidos.filter((p) => {
    if (pestanaEstado === 'PENDIENTES') return p.estado === ESTADO_PEDIDO.PENDIENTE;
    if (pestanaEstado === 'ENTREGADOS') return p.estado === ESTADO_PEDIDO.ENTREGADO;
    return true;
  });

  function fijarRango(dias) {
    const hoy = obtenerFechaDeHoy();
    if (dias === 0) {
      cambiarFiltro('desde')({ target: { value: hoy } });
      cambiarFiltro('hasta')({ target: { value: hoy } });
    } else if (dias === 7) {
      const d = new Date();
      d.setDate(d.getDate() - 7);
      const desde = d.toISOString().split('T')[0];
      cambiarFiltro('desde')({ target: { value: desde } });
      cambiarFiltro('hasta')({ target: { value: hoy } });
    } else if (dias === -1) {
      cambiarFiltro('desde')({ target: { value: '' } });
      cambiarFiltro('hasta')({ target: { value: '' } });
    }
    setPagina(0);
  }

  function cambiarFiltroConPagina(campo) {
    return (evento) => {
      cambiarFiltro(campo)(evento);
      setPagina(0);
    };
  }

  function buscar(evento) {
    evento.preventDefault();
    conAviso(async () => setBuscado(await obtenerPedido(numero)));
  }

  function verTodos() {
    setBuscado(null);
    setNumero('');
    setPagina(0);
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
      <Encabezado
        titulo="Pedidos"
        subtitulo="Control de comandas de mostrador y entregas a domicilio"
      >
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
        <input type="date" aria-label="Desde" value={filtros.desde} onChange={cambiarFiltroConPagina('desde')} />
        <input type="date" aria-label="Hasta" value={filtros.hasta} onChange={cambiarFiltroConPagina('hasta')} />
        <select
          aria-label="Tipo de entrega"
          value={filtros.tipoDeEntrega}
          onChange={cambiarFiltroConPagina('tipoDeEntrega')}
        >
          <option value="">Mostrador y delivery</option>
          <option value={TIPO_ENTREGA.MOSTRADOR}>Solo mostrador</option>
          <option value={TIPO_ENTREGA.DELIVERY}>Solo delivery</option>
        </select>
        <div style={{ display: 'inline-flex', gap: '0.35rem' }}>
          <button type="button" className="btn btn-ghost btn-chico" onClick={() => fijarRango(0)}>
            Hoy
          </button>
          <button type="button" className="btn btn-ghost btn-chico" onClick={() => fijarRango(7)}>
            7 días
          </button>
          <button type="button" className="btn btn-ghost btn-chico" onClick={() => fijarRango(-1)}>
            Todo
          </button>
        </div>
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
      {datos && (
        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.25rem', flexWrap: 'wrap' }}>
          <button
            type="button"
            className={`btn btn-chico ${pestanaEstado === 'TODOS' ? 'btn-primario' : 'btn-ghost'}`}
            onClick={() => setPestanaEstado('TODOS')}
          >
            Todos ({pedidos.length})
          </button>
          <button
            type="button"
            className={`btn btn-chico ${pestanaEstado === 'PENDIENTES' ? 'btn-primario' : 'btn-ghost'}`}
            onClick={() => setPestanaEstado('PENDIENTES')}
            style={pestanaEstado === 'PENDIENTES' ? {} : { color: 'var(--caramelo)', borderColor: 'var(--caramelo)' }}
          >
            ⏳ En preparación ({cantidadPendientes})
          </button>
          <button
            type="button"
            className={`btn btn-chico ${pestanaEstado === 'ENTREGADOS' ? 'btn-primario' : 'btn-ghost'}`}
            onClick={() => setPestanaEstado('ENTREGADOS')}
          >
            ✓ Entregados ({pedidos.length - cantidadPendientes})
          </button>
        </div>
      )}

      {datos && pedidosVisibles.length === 0 && (
        <p className="estado-vacio">
          No hay pedidos {pestanaEstado === 'PENDIENTES' ? 'pendientes' : 'para mostrar'}.
        </p>
      )}
      <div className="tickets">
        {pedidosVisibles.map((pedido) => (
          <TarjetaPedido
            key={pedido.id}
            pedido={pedido}
            ocupado={idOcupado === pedido.id}
            alMarcarEntregado={marcarEntregado}
            alEliminar={setAEliminar}
          />
        ))}
      </div>

      {datos && datos.totalPaginas > 1 && !buscado && (
        <nav className="paginacion" aria-label="Paginación de pedidos">
          <button
            type="button"
            className="btn btn-ghost btn-chico"
            disabled={pagina === 0}
            onClick={() => setPagina((p) => Math.max(0, p - 1))}
          >
            ← Anterior
          </button>
          <span className="paginacion-texto">
            Página {pagina + 1} de {datos.totalPaginas} ({datos.totalElementos} pedidos)
          </span>
          <button
            type="button"
            className="btn btn-ghost btn-chico"
            disabled={pagina >= datos.totalPaginas - 1}
            onClick={() => setPagina((p) => p + 1)}
          >
            Siguiente →
          </button>
        </nav>
      )}

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
