import { formatearPesos } from '../utils/formato.js';

export default function Ticket({ cabecera, pie, children, entregado = false }) {
  return (
    <article className={`ticket ${entregado ? 'ticket-entregado' : ''}`}>
      <header className="ticket-cabecera">{cabecera}</header>
      <div className="ticket-cuerpo">{children}</div>
      <footer className="ticket-pie">{pie}</footer>
    </article>
  );
}

export function TotalTicket({ monto }) {
  return (
    <div className="ticket-total">
      <span>Total</span>
      <strong>{formatearPesos(monto)}</strong>
    </div>
  );
}
