import { formatearPesos } from '../utils/formato.js';

export default function LineasPedido({ lineas, alCambiarCantidad }) {
  if (lineas.length === 0) {
    return <p className="ticket-vacio">Tocá un producto para sumarlo.</p>;
  }
  return (
    <ul className="lineas">
      {lineas.map(({ producto, cantidad }) => (
        <li key={producto.id}>
          <span className="contador">
            <button
              type="button"
              aria-label={`Quitar uno de ${producto.nombre}`}
              onClick={() => alCambiarCantidad(producto, -1)}
            >
              −
            </button>
            <span>{cantidad}</span>
            <button
              type="button"
              aria-label={`Sumar uno de ${producto.nombre}`}
              onClick={() => alCambiarCantidad(producto, 1)}
            >
              +
            </button>
          </span>
          <span>{producto.nombre}</span>
          <span className="lineas-monto">{formatearPesos(producto.precio)} c/u</span>
        </li>
      ))}
    </ul>
  );
}
