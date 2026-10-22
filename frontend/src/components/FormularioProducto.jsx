import { useState } from 'react';
import useFormulario from '../hooks/useFormulario.js';
import { CATEGORIAS } from '../utils/dominio.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { validarProducto } from '../utils/validaciones.js';
import Aviso from './Aviso.jsx';
import Campo from './Campo.jsx';
import Modal from './Modal.jsx';
import SelectCategoria from './SelectCategoria.jsx';

export default function FormularioProducto({ producto, alGuardar, alCerrar }) {
  const { valores, cambiar } = useFormulario({
    nombre: producto.nombre ?? '',
    categoria: producto.categoria ?? Object.keys(CATEGORIAS)[0],
    precio: producto.precio ?? '',
    stock: producto.stock ?? '',
    descripcion: producto.descripcion ?? '',
  });
  const [error, setError] = useState('');
  const [guardando, setGuardando] = useState(false);
  const esNuevo = !producto.id;

  async function guardar(evento) {
    evento.preventDefault();
    const datos = {
      nombre: valores.nombre.trim(),
      categoria: valores.categoria,
      precio: Number(valores.precio),
      stock: Number(valores.stock),
      descripcion: valores.descripcion.trim() || null,
    };
    const errorValidacion = validarProducto(datos);
    setError(errorValidacion);
    if (errorValidacion) {
      return;
    }
    setGuardando(true);
    try {
      await alGuardar(datos);
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      setGuardando(false);
    }
  }

  return (
    <Modal titulo={esNuevo ? 'Nuevo producto' : `Editar ${producto.nombre}`} alCerrar={alCerrar}>
      <form className="formulario" onSubmit={guardar} noValidate>
        <Campo etiqueta="Nombre">
          <input value={valores.nombre} onChange={cambiar('nombre')} />
        </Campo>
        <Campo etiqueta="Categoría">
          <SelectCategoria valor={valores.categoria} alCambiar={cambiar('categoria')} />
        </Campo>
        <div className="formulario-fila">
          <Campo etiqueta="Precio ($)">
            <input type="number" min="1" step="any" value={valores.precio} onChange={cambiar('precio')} />
          </Campo>
          <Campo etiqueta="Stock">
            <input type="number" min="1" step="1" value={valores.stock} onChange={cambiar('stock')} />
          </Campo>
        </div>
        <Campo etiqueta="Descripción (opcional)">
          <input value={valores.descripcion} onChange={cambiar('descripcion')} />
        </Campo>
        <Aviso error={error} />
        <div className="modal-acciones">
          <button type="button" className="btn btn-ghost" onClick={alCerrar}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primario" disabled={guardando}>
            {esNuevo ? 'Crear producto' : 'Guardar cambios'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
