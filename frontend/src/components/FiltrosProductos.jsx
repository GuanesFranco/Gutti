import { OPCIONES_CATEGORIA } from '../utils/dominio.js';
import Segmentos from './Segmentos.jsx';

export default function FiltrosProductos({ filtros, alCambiar }) {
  return (
    <div className="filtros">
      <input
        type="search"
        className="buscador"
        placeholder="Buscar producto"
        aria-label="Buscar producto"
        value={filtros.nombre}
        onChange={(evento) => alCambiar('nombre', evento.target.value)}
      />
      <Segmentos
        opciones={OPCIONES_CATEGORIA}
        valor={filtros.categoria}
        alCambiar={(valor) => alCambiar('categoria', valor)}
        etiqueta="Categoría"
      />
    </div>
  );
}
