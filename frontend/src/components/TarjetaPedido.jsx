import { Link } from 'react-router-dom';
import { ESTADO_PEDIDO, linkWhatsAppPedido, TIPO_ENTREGA } from '../utils/dominio.js';
import { formatearFechaHora, formatearPesos } from '../utils/formato.js';
import { rutaEditarPedido } from '../utils/rutas.js';
import Ticket, { TotalTicket } from './Ticket.jsx';

export default function TarjetaPedido({ pedido, ocupado, alMarcarEntregado, alEliminar, alReordenar }) {
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
          <span className="chip-dot" aria-hidden="true" />
          {entregado ? 'Entregado' : 'Pendiente'}
        </span>
      </div>
      <div className="ticket-cliente">
        <span>{pedido.nombreCliente}</span>
        <span>{pedido.telefono}</span>
        {esDelivery && pedido.direccion && (
          <span className="ticket-direccion">{pedido.direccion}</span>
        )}
      </div>
    </>
  );

  const pie = (
    <>
      <TotalTicket monto={pedido.total} />
      {!entregado && (Boolean(alMarcarEntregado) || Boolean(alEliminar)) && (
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
      {!entregado && !alMarcarEntregado && !alEliminar && (
        <div className="ticket-acciones">
          <a
            href={linkWhatsAppPedido(pedido.id)}
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-ghost btn-chico"
            style={{
              width: '100%',
              color: 'var(--caramelo)',
              borderColor: 'var(--borde)',
              fontWeight: 600,
              textAlign: 'center',
            }}
          >
            Modificar por WhatsApp
          </a>
        </div>
      )}
      {entregado && Boolean(alReordenar) && (
        <div className="ticket-acciones">
          <button
            type="button"
            className="btn btn-primario btn-chico"
            style={{ width: '100%' }}
            onClick={() => alReordenar(pedido)}
          >
            Volver a pedir
          </button>
        </div>
      )}
    </>
  );

  return (
    <Ticket entregado={entregado} cabecera={cabecera} pie={pie}>
      <ul className="lineas">
        {pedido.items.map((item, indice) => (
          <li key={`${item.nombreProducto}-${indice}`}>
            <span className="lineas-cantidad">{item.cantidad}×</span>
            <div style={{ flex: 1, minWidth: 0 }}>
              <span>{item.nombreProducto}</span>
              {item.gustos && (
                <small
                  className="subtexto"
                  style={{
                    display: 'block',
                    color: 'var(--suave)',
                    fontSize: '0.78rem',
                    marginTop: '2px',
                  }}
                >
                  Gustos: {item.gustos}
                </small>
              )}
            </div>
            <span className="lineas-monto">{formatearPesos(item.subtotal)}</span>
          </li>
        ))}
      </ul>
    </Ticket>
  );
}
