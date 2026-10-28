import { formatearPesos } from '../utils/formato.js';

export default function LineasPedido({ lineas, alCambiarCantidad }) {
  if (lineas.length === 0) {
    return <p className="ticket-vacio">Tocá un producto para sumarlo.</p>;
  }
  return (
    <ul className="lineas">
      {lineas.map(({ producto, cantidad, gustos }) => (
        <li key={producto.id + (gustos ? `__${gustos}` : '')}>
          <span className="contador">
            <button
              type="button"
              aria-label={`Quitar uno de ${producto.nombre}`}
              onClick={() => alCambiarCantidad(producto, -1, gustos)}
            >
              −
            </button>
            <span>{cantidad}</span>
            <button
              type="button"
              aria-label={`Sumar uno de ${producto.nombre}`}
              onClick={() => alCambiarCantidad(producto, 1, gustos)}
            >
              +
            </button>
          </span>
          <div style={{ flex: 1, minWidth: 0 }}>
            <span>{producto.nombre}</span>
            {gustos && (
              <small
                className="subtexto"
                style={{
                  display: 'block',
                  color: 'var(--suave)',
                  fontSize: '0.78rem',
                  marginTop: '2px',
                }}
              >
                Gustos: {gustos}
              </small>
            )}
          </div>
          <span className="lineas-monto">{formatearPesos(producto.precio)} c/u</span>
        </li>
      ))}
    </ul>
  );
}
