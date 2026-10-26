import Ticket, { TotalTicket } from './Ticket.jsx';

export default function PedidoConfirmado({ pedido, alHacerOtro }) {
  return (
    <div className="confirmacion">
      <Ticket
        cabecera={<strong className="ticket-numero">Pedido #{pedido.id}</strong>}
        pie={<TotalTicket monto={pedido.total} />}
      >
        <p>Recibimos tu pedido. Guardá el número por si tenés que consultarlo.</p>
      </Ticket>
      <button type="button" className="btn btn-primario" onClick={alHacerOtro}>
        Hacer otro pedido
      </button>
    </div>
  );
}
