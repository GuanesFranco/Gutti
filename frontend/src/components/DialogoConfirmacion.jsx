import { useState } from 'react';
import { obtenerMensajeDeError } from '../utils/errores.js';
import Aviso from './Aviso.jsx';
import Modal from './Modal.jsx';

export default function DialogoConfirmacion({ titulo, texto, textoAccion, alConfirmar, alCerrar }) {
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState('');

  async function confirmar() {
    setEnviando(true);
    try {
      await alConfirmar();
      alCerrar();
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      setEnviando(false);
    }
  }

  return (
    <Modal titulo={titulo} alCerrar={alCerrar}>
      <p className="texto-suave">{texto}</p>
      <Aviso error={error} />
      <div className="modal-acciones">
        <button type="button" className="btn btn-ghost" onClick={alCerrar}>
          Cancelar
        </button>
        <button type="button" className="btn btn-peligro" disabled={enviando} onClick={confirmar}>
          {textoAccion}
        </button>
      </div>
    </Modal>
  );
}
