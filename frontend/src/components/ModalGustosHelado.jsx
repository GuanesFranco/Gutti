import { useState } from 'react';
import { FAMILIAS_GUSTOS, maxGustosPermitidos } from '../utils/helados.js';
import Modal from './Modal.jsx';

export default function ModalGustosHelado({ producto, alConfirmar, alCerrar }) {
  const [seleccionados, setSeleccionados] = useState([]);
  const max = maxGustosPermitidos(producto);

  function alternarGusto(gusto) {
    if (seleccionados.includes(gusto)) {
      setSeleccionados(seleccionados.filter((g) => g !== gusto));
    } else {
      if (seleccionados.length < max) {
        setSeleccionados([...seleccionados, gusto]);
      }
    }
  }

  function confirmar(evento) {
    evento.preventDefault();
    if (seleccionados.length === 0) return;
    alConfirmar(seleccionados.join(', '));
  }

  return (
    <Modal titulo={`Elegí los gustos de tu ${producto.nombre}`} alCerrar={alCerrar}>
      <form onSubmit={confirmar}>
        <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <p className="texto-suave" style={{ margin: 0 }}>
            Podés elegir hasta <strong>{max} {max === 1 ? 'gusto' : 'gustos'}</strong>:
          </p>
          <span
            className={`chip ${seleccionados.length === max ? 'chip-ok' : ''}`}
            style={{ fontWeight: 700 }}
          >
            {seleccionados.length} de {max}
          </span>
        </div>

        <div
          style={{
            maxHeight: '340px',
            overflowY: 'auto',
            paddingRight: '0.25rem',
            marginBottom: '1.25rem',
            display: 'flex',
            flexDirection: 'column',
            gap: '1.1rem',
          }}
        >
          {FAMILIAS_GUSTOS.map((familia) => (
            <div key={familia.titulo}>
              <h3
                style={{
                  fontSize: '0.8rem',
                  fontWeight: 700,
                  color: 'var(--caramelo)',
                  textTransform: 'uppercase',
                  letterSpacing: '0.04em',
                  marginBottom: '0.5rem',
                }}
              >
                {familia.titulo}
              </h3>
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                  gap: '0.5rem',
                }}
              >
                {familia.gustos.map((gusto) => {
                  const elegido = seleccionados.includes(gusto);
                  const deshabilitado = !elegido && seleccionados.length >= max;
                  return (
                    <label
                      key={gusto}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.6rem',
                        padding: '0.6rem 0.85rem',
                        borderRadius: '10px',
                        border: `1.5px solid ${elegido ? 'var(--verde)' : 'var(--borde)'}`,
                        background: elegido ? 'rgba(184, 107, 39, 0.08)' : 'var(--superficie)',
                        cursor: deshabilitado ? 'not-allowed' : 'pointer',
                        opacity: deshabilitado ? 0.45 : 1,
                        transition: 'all 0.15s ease',
                        userSelect: 'none',
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={elegido}
                        disabled={deshabilitado}
                        onChange={() => alternarGusto(gusto)}
                        style={{ accentColor: 'var(--verde)', width: '16px', height: '16px' }}
                      />
                      <span style={{ fontSize: '0.88rem', fontWeight: elegido ? 600 : 400 }}>{gusto}</span>
                    </label>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        <div className="botonera">
          <button type="button" className="btn btn-ghost" onClick={alCerrar}>
            Cancelar
          </button>
          <button
            type="submit"
            className="btn btn-primario"
            disabled={seleccionados.length === 0}
          >
            Agregar al pedido ({seleccionados.length}/{max})
          </button>
        </div>
      </form>
    </Modal>
  );
}

