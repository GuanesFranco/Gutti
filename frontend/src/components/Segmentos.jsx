export default function Segmentos({ opciones, valor, alCambiar, etiqueta, ancho = false }) {
  return (
    <div className={`segmentos ${ancho ? 'segmentos-ancho' : ''}`} role="group" aria-label={etiqueta}>
      {opciones.map((opcion) => (
        <button
          key={opcion.texto}
          type="button"
          className={opcion.valor === valor ? 'activo' : ''}
          aria-pressed={opcion.valor === valor}
          onClick={() => alCambiar(opcion.valor)}
        >
          {opcion.texto}
        </button>
      ))}
    </div>
  );
}
