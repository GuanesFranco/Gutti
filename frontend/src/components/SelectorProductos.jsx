import { nombrarCategoria } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import FotoProducto from './FotoProducto.jsx';

export default function SelectorProductos({ productos, cantidades, alSumar }) {
  if (productos.length === 0) {
    return <p className="estado-vacio">No hay productos que coincidan.</p>;
  }
  return (
    <ul className="grilla-productos">
      {productos.map((producto) => {
        const cantidad = cantidades[producto.id] || 0;
        return (
          <li key={producto.id} className={`producto ${cantidad ? 'producto-elegido' : ''}`}>
            <button type="button" className="producto-boton" onClick={() => alSumar(producto)}>
              <FotoProducto nombre={producto.nombre} />
              <span className="producto-categoria">{nombrarCategoria(producto.categoria)}</span>
              <span className="producto-nombre">{producto.nombre}</span>
              <span className="producto-precio">{formatearPesos(producto.precio)}</span>
              <span className="producto-stock">Stock: {producto.stock}</span>
              {cantidad > 0 && <span className="solo-lector">, {cantidad} en el pedido</span>}
            </button>
            {cantidad > 0 && (
              <span className="producto-contador" aria-hidden="true">
                {cantidad}
              </span>
            )}
          </li>
        );
      })}
    </ul>
  );
}
