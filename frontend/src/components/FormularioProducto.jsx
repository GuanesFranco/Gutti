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
    };
    await enviar(() => alGuardar(datos));
  }

  return (
    <Modal titulo={esNuevo ? 'Nuevo producto' : `Editar ${producto.nombre}`} alCerrar={alCerrar}>
      <form className="formulario" onSubmit={guardar} noValidate>
        <Campo etiqueta="Nombre">
          <input value={valores.nombre} onChange={cambiar('nombre')} />
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
          <input value={valores.descripcion} onChange={cambiar('descripcion')} />
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
