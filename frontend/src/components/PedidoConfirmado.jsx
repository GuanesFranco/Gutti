import { Link } from 'react-router-dom';
import { linkWhatsAppPedido, WHATSAPP_LOCAL } from '../utils/dominio.js';
import { RUTAS } from '../utils/rutas.js';
import Ticket, { TotalTicket } from './Ticket.jsx';

export default function PedidoConfirmado({ pedido, alHacerOtro }) {
  const esDelivery = Boolean(pedido.direccion);

  return (
    <div className="confirmacion" style={{ maxWidth: '520px', margin: '2rem auto' }}>
      <Ticket
        cabecera={<strong className="ticket-numero">Pedido #{pedido.id}</strong>}
        pie={<TotalTicket monto={pedido.total} />}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', padding: '0.4rem 0' }}>
          <div>
            <p style={{ fontWeight: 700, fontSize: '1.1rem', margin: '0 0 0.35rem', color: 'var(--verde)' }}>
              ¡Tu pedido fue recibido con éxito!
            </p>
            <p className="texto-suave" style={{ margin: 0, fontSize: '0.92rem' }}>
              Guardá el número <strong>#{pedido.id}</strong> para cualquier consulta o seguimiento.
            </p>
          </div>

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '0.85rem 0.75rem',
              background: 'var(--superficie-2)',
              borderRadius: '12px',
              border: '1px solid var(--borde)',
            }}
          >
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1, zIndex: 1 }}>
              <div
                style={{
                  width: '26px',
                  height: '26px',
                  borderRadius: '50%',
                  background: 'var(--verde)',
                  color: '#fff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: 700,
                  fontSize: '0.78rem',
                }}
              >
                1
              </div>
              <span style={{ fontSize: '0.73rem', fontWeight: 700, marginTop: '4px', color: 'var(--verde)' }}>
                Recibido
              </span>
            </div>

            <div
              style={{
                height: '2px',
                flex: 1,
                background: 'var(--caramelo)',
                opacity: 0.4,
                margin: '0 -4px 16px',
              }}
            />

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1, zIndex: 1 }}>
              <div
                style={{
                  width: '26px',
                  height: '26px',
                  borderRadius: '50%',
                  background: 'var(--caramelo)',
                  color: '#fff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: 700,
                  fontSize: '0.78rem',
                  boxShadow: '0 0 0 3px var(--caramelo-suave)',
                }}
              >
                2
              </div>
              <span style={{ fontSize: '0.73rem', fontWeight: 700, marginTop: '4px', color: 'var(--caramelo)' }}>
                Preparando
              </span>
            </div>

            <div
              style={{
                height: '2px',
                flex: 1,
                background: 'var(--borde)',
                margin: '0 -4px 16px',
              }}
            />

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1, zIndex: 1 }}>
              <div
                style={{
                  width: '26px',
                  height: '26px',
                  borderRadius: '50%',
                  background: 'var(--superficie)',
                  border: '1.5px solid var(--borde)',
                  color: 'var(--suave)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.78rem',
                }}
              >
                3
              </div>
              <span style={{ fontSize: '0.73rem', color: 'var(--suave)', marginTop: '4px' }}>
                {esDelivery ? 'En camino' : 'Listo'}
              </span>
            </div>
          </div>

          {esDelivery && (
            <p className="texto-suave" style={{ margin: 0, fontSize: '0.9rem' }}>
              Envío a domicilio: <strong>{pedido.direccion}</strong>
            </p>
          )}

          <div
            style={{
              background: 'var(--superficie-2)',
              borderRadius: '12px',
              padding: '1rem',
              border: '1px solid var(--borde)',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.9rem',
            }}
          >
            <div>
              <strong style={{ display: 'block', fontSize: '0.9rem', color: 'var(--texto)' }}>
                Medio de pago: Solo efectivo
              </strong>
              <span className="texto-suave" style={{ fontSize: '0.82rem', display: 'block', marginTop: '2px', lineHeight: 1.35 }}>
                {esDelivery
                  ? 'Abonás en efectivo cuando el repartidor te entregue el pedido.'
                  : 'Abonás en efectivo directamente en el mostrador al retirar tu pedido.'}
              </span>
            </div>

            <div style={{ borderTop: '1px solid var(--borde)', paddingTop: '0.75rem' }}>
              <strong style={{ display: 'block', fontSize: '0.9rem', color: 'var(--texto)' }}>
                ¿Necesitás modificar tu pedido?
              </strong>
              <span className="texto-suave" style={{ fontSize: '0.82rem', display: 'block', marginTop: '2px', lineHeight: 1.35 }}>
                Comunicate con el local al <strong>{WHATSAPP_LOCAL.numero}</strong> indicando tu número de pedido (<strong>#{pedido.id}</strong>).
              </span>
                <a
                  href={linkWhatsAppPedido(pedido.id)}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="btn btn-ghost btn-chico"
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '0.4rem',
                    marginTop: '0.5rem',
                    color: 'var(--caramelo)',
                    borderColor: 'var(--borde)',
                    fontWeight: 600,
                  }}
                >
                  Escribir al WhatsApp del local
                </a>
              </div>
            </div>
          </div>
      </Ticket>

      <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center', marginTop: '1.25rem', flexWrap: 'wrap' }}>
        <button type="button" className="btn btn-primario" onClick={alHacerOtro}>
          Hacer otro pedido
        </button>
        <Link to={RUTAS.MIS_PEDIDOS} className="btn btn-ghost">
          Ver mis pedidos
        </Link>
      </div>
    </div>
  );
}
