import { useEffect, useId, useRef } from 'react';

export default function Modal({ titulo, alCerrar, children }) {
  const dialogo = useRef(null);
  const idTitulo = useId();

  useEffect(() => {
    const elemento = dialogo.current;
    elemento.showModal();
    return () => elemento.close();
  }, []);

  function cerrarSiTocaElFondo(evento) {
    if (evento.target === dialogo.current) {
      alCerrar();
    }
  }

  return (
    <dialog
      ref={dialogo}
      className="modal"
      aria-labelledby={idTitulo}
      onCancel={(evento) => {
        evento.preventDefault();
        alCerrar();
      }}
      onClick={cerrarSiTocaElFondo}
    >
      <div className="modal-contenido">
        <div className="modal-cabecera">
          <h2 id={idTitulo}>{titulo}</h2>
          <button type="button" className="btn-icono" onClick={alCerrar} aria-label="Cerrar">
            ✕
          </button>
        </div>
        {children}
      </div>
    </dialog>
  );
}
