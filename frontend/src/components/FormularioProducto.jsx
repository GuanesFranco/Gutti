import useEnvio from '../hooks/useEnvio.js';
import useFormulario from '../hooks/useFormulario.js';
import { CATEGORIAS } from '../utils/dominio.js';
import Aviso from './Aviso.jsx';
import Campo from './Campo.jsx';
import Modal from './Modal.jsx';
import SelectorCategoria from './SelectorCategoria.jsx';

// Los campos vacíos van como null: el mensaje lo escribe el backend.
function aNumero(texto) {
  if (texto === '') {
    return null;
  }
  return Number(texto);
}

export default function FormularioProducto({ producto, alGuardar, alCerrar }) {
  const { valores, cambiar } = useFormulario({
    nombre: producto.nombre ?? '',
    categoria: producto.categoria ?? Object.keys(CATEGORIAS)[0],
    precio: producto.precio ?? '',
    stock: producto.stock ?? '',
    descripcion: producto.descripcion ?? '',
    imagenUrl: producto.imagenUrl ?? '',
  });
  const { enviar, enviando, error } = useEnvio();
  const esNuevo = !producto.id;

  async function guardar(evento) {
    evento.preventDefault();
    const datos = {
      nombre: valores.nombre.trim(),
      categoria: valores.categoria,
      precio: aNumero(valores.precio),
      stock: aNumero(valores.stock),
      descripcion: valores.descripcion.trim() || null,
      imagenUrl: valores.imagenUrl.trim() || null,
    };
    await enviar(() => alGuardar(datos));
  }

  const tieneUrlImagen = Boolean(valores.imagenUrl?.trim());

  return (
    <Modal titulo={esNuevo ? 'Nuevo producto' : `Editar ${producto.nombre}`} alCerrar={alCerrar}>
      <form className="formulario" onSubmit={guardar} noValidate>
        <Campo etiqueta="Nombre">
          <input
            value={valores.nombre}
            onChange={cambiar('nombre')}
            placeholder="Ej. Torta de Frutilla"
            autoFocus
          />
        </Campo>
        <Campo etiqueta="Categoría">
          <SelectorCategoria valor={valores.categoria} alCambiar={cambiar('categoria')} />
        </Campo>
        <div className="formulario-fila">
          <Campo etiqueta="Precio ($)">
            <input type="number" step="any" value={valores.precio} onChange={cambiar('precio')} />
          </Campo>
          <Campo etiqueta="Stock">
            <input type="number" step="1" value={valores.stock} onChange={cambiar('stock')} />
          </Campo>
        </div>
        <Campo etiqueta="Descripción (opcional)">
          <input
            value={valores.descripcion}
            onChange={cambiar('descripcion')}
            placeholder="Ej. Con crema pastelera y frutillas frescas"
          />
        </Campo>
        <Campo etiqueta="URL de la imagen (opcional)">
          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <input
              style={{ flex: 1 }}
              value={valores.imagenUrl}
              onChange={cambiar('imagenUrl')}
              placeholder="https://ejemplo.com/foto-torta.jpg"
            />
            {tieneUrlImagen && (
              <img
                src={valores.imagenUrl.trim()}
                alt="Vista previa"
                style={{
                  width: '42px',
                  height: '42px',
                  borderRadius: '8px',
                  objectFit: 'cover',
                  border: '1px solid var(--borde)',
                  background: 'var(--superficie-2)',
                  flexShrink: 0,
                }}
                onError={(e) => {
                  e.currentTarget.style.display = 'none';
                }}
                onLoad={(e) => {
                  e.currentTarget.style.display = 'block';
                }}
              />
            )}
          </div>
        </Campo>
        <Aviso error={error} />
        <div className="botonera">
          <button type="button" className="btn btn-ghost" onClick={alCerrar}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primario" disabled={enviando}>
            {esNuevo ? 'Crear producto' : 'Guardar cambios'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
