import { useState } from 'react';
import { nombrarCategoria, OPCIONES_CATEGORIA } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import FotoProducto from './FotoProducto.jsx';
import Segmentos from './Segmentos.jsx';

export default function SelectorProductos({ productos, cantidades, alSumar }) {
  const [busqueda, setBusqueda] = useState('');
  const [categoria, setCategoria] = useState('');

  const visibles = productos.filter(
    (producto) =>
      producto.nombre.toLowerCase().includes(busqueda.trim().toLowerCase()) &&
      (!categoria || producto.categoria === categoria),
  );

  return (
    <section aria-label="Productos">
      <div className="filtros">
        <input
          type="search"
          className="buscador"
          placeholder="Buscar producto"
          aria-label="Buscar producto"
          value={busqueda}
          onChange={(evento) => setBusqueda(evento.target.value)}
        />
        <Segmentos opciones={OPCIONES_CATEGORIA} valor={categoria} alCambiar={setCategoria} etiqueta="Categoría" />
      </div>
      {visibles.length === 0 && <p className="estado-vacio">No hay productos que coincidan.</p>}
      <ul className="grilla-productos">
        {visibles.map((producto) => {
          const cantidad = cantidades[producto.id] || 0;
          const quedan = producto.stock - cantidad;
          return (
            <li key={producto.id} className={`producto ${cantidad ? 'producto-elegido' : ''}`}>
              <button type="button" className="producto-boton" disabled={quedan <= 0} onClick={() => alSumar(producto)}>
                <FotoProducto nombre={producto.nombre} />
                <span className="producto-categoria">{nombrarCategoria(producto.categoria)}</span>
                <span className="producto-nombre">{producto.nombre}</span>
                <span className="producto-precio">{formatearPesos(producto.precio)}</span>
                <span className="producto-stock">{quedan > 0 ? `Quedan ${quedan}` : 'Sin stock'}</span>
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
    </section>
  );
}
