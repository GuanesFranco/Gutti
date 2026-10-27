import { CATEGORIAS } from '../utils/dominio.js';

export default function SelectorCategoria({ valor, alCambiar, textoTodas }) {
  return (
    <select aria-label="Categoría" value={valor} onChange={alCambiar}>
      {textoTodas && <option value="">{textoTodas}</option>}
      {Object.entries(CATEGORIAS).map(([clave, nombre]) => (
        <option key={clave} value={clave}>
          {nombre}
        </option>
      ))}
    </select>
  );
}
