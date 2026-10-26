export default function Segmentos({ opciones, valor, alCambiar, etiqueta, ancho = false }) {
  return (
    <div className={`segmentos ${ancho ? 'segmentos-ancho' : ''}`} role="group" aria-label={etiqueta}>
      {opciones.map((opcion) => {
        const elegida = opcion.valor === valor;
        return (
          <button
            key={opcion.texto}
            type="button"
            className={elegida ? 'activo' : ''}
            aria-pressed={elegida}
            onClick={() => alCambiar(opcion.valor)}
          >
            {opcion.texto}
          </button>
        );
      })}
    </div>
  );
}
