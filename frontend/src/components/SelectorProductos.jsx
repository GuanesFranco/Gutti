import { nombrarCategoria } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import FotoProducto from './FotoProducto.jsx';



export default function SelectorProductos({ productos, cantidades, alSumar, alRestar }) {
  if (productos.length === 0) {
    return <p className="estado-vacio">No hay productos que coincidan.</p>;
  }
  return (
    <ul className="grilla-productos">
      {productos.map((producto) => {
        const cantidad = cantidades[producto.id] || 0;
        const sinStock = producto.stock <= 0;
        return (
          <li key={producto.id} className={`producto ${cantidad ? 'producto-elegido' : ''}`}>
            <button
              type="button"
              className="producto-boton"
              disabled={sinStock}
              onClick={() => alSumar(producto)}
            >
              <div className="producto-foto-wrap">
                <FotoProducto nombre={producto.nombre} imagenUrl={producto.imagenUrl} />
                <span className="producto-categoria">{nombrarCategoria(producto.categoria)}</span>
              </div>
              <span className="producto-nombre">{producto.nombre}</span>
              <div className="producto-pie-tarjeta">
                <div className="producto-precios-stock">
                  <span className="producto-precio">{formatearPesos(producto.precio)}</span>
                  {sinStock && <span className="producto-stock stock-bajo">Agotado</span>}



                </div>
                {cantidad === 0 && !sinStock && (
                  <span className="producto-btn-sumar-pill">+ Agregar</span>
                )}
              </div>
              {cantidad > 0 && <span className="solo-lector">, {cantidad} en el pedido</span>}
            </button>
            {cantidad > 0 && (
              <div className="producto-control-cantidad" aria-label={`Cantidad de ${producto.nombre}`}>
                {alRestar && (
                  <button
                    type="button"
                    className="producto-btn-paso"
                    aria-label={`Quitar uno de ${producto.nombre}`}
                    onClick={(evento) => {
                      evento.stopPropagation();
                      alRestar(producto);
                    }}
                  >
                    −
                  </button>
                )}
                <span className="producto-contador" aria-hidden="true">
                  {cantidad}
                </span>
                {alSumar && (
                  <button
                    type="button"
                    className="producto-btn-paso"
                    aria-label={`Sumar uno de ${producto.nombre}`}
                    disabled={cantidad >= producto.stock}
                    onClick={(evento) => {
                      evento.stopPropagation();
                      alSumar(producto);
                    }}
                  >
                    +
                  </button>
                )}
              </div>
            )}
          </li>
        );
      })}
    </ul>
  );
}

