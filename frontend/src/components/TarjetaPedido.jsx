import { Link } from 'react-router-dom';
import { ESTADO_PEDIDO, TIPO_ENTREGA } from '../utils/dominio.js';
import { formatearFechaHora, formatearPesos } from '../utils/formato.js';
import { rutaEditarPedido } from '../utils/rutas.js';
import Ticket, { TotalTicket } from './Ticket.jsx';

export default function TarjetaPedido({ pedido, ocupado, alMarcarEntregado, alEliminar }) {
  const entregado = pedido.estado === ESTADO_PEDIDO.ENTREGADO;
  const esDelivery = pedido.tipoDeEntrega === TIPO_ENTREGA.DELIVERY;

  const cabecera = (
    <>
      <div className="ticket-titulo">
        <strong className="ticket-numero">#{pedido.id}</strong>
        <span className={`etiqueta ${esDelivery ? 'etiqueta-delivery' : ''}`}>
          {esDelivery ? 'Delivery' : 'Mostrador'}
        </span>
      </div>
      <div className="ticket-datos">
        <time>{formatearFechaHora(pedido.fechaHora)}</time>
        <span className={`chip ${entregado ? 'chip-ok' : 'chip-pendiente'}`}>
          {entregado ? 'Entregado' : 'Pendiente'}
        </span>
      </div>
      <div className="ticket-cliente">
        <span>{pedido.nombreCliente}</span>
        <span>{pedido.telefono}</span>
      </div>
    </>
  );

  const pie = (
    <>
      <TotalTicket monto={pedido.total} />
      {!entregado && (
        <div className="ticket-acciones">
          <button
            type="button"
            className="btn btn-primario btn-chico"
            disabled={ocupado}
            onClick={() => alMarcarEntregado(pedido)}
          >
            Marcar entregado
          </button>
          <Link to={rutaEditarPedido(pedido.id)} className="btn btn-ghost btn-chico">
            Editar
          </Link>
          <button
            type="button"
            className="btn btn-ghost btn-chico texto-peligro"
            disabled={ocupado}
            onClick={() => alEliminar(pedido)}
          >
            Eliminar
          </button>
        </div>
      )}
    </>
  );

  return (
    <Ticket entregado={entregado} cabecera={cabecera} pie={pie}>
      <ul className="lineas">
        {pedido.items.map((item, indice) => (
          <li key={indice}>
            <span className="lineas-cantidad">{item.cantidad}×</span>
            <span>{item.nombreProducto}</span>
            <span className="lineas-monto">{formatearPesos(item.subtotal)}</span>
          </li>
        ))}
      </ul>
    </Ticket>
  );
}
