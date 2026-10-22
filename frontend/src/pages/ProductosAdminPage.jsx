import { useCallback, useState } from 'react';
import {
  activarProducto,
  crearProducto,
  darDeBajaProducto,
  listarProductos,
  modificarProducto,
} from '../api/productos.js';
import Aviso from '../components/Aviso.jsx';
import DialogoConfirmacion from '../components/DialogoConfirmacion.jsx';
import FormularioProducto from '../components/FormularioProducto.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import SelectCategoria from '../components/SelectCategoria.jsx';
import useCarga from '../hooks/useCarga.js';
import useFormulario from '../hooks/useFormulario.js';
import { nombrarCategoria, ORDEN_STOCK } from '../utils/dominio.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { formatearPesos } from '../utils/formato.js';

const STOCK_BAJO = 5;

export default function ProductosAdminPage() {
  const notificar = useNotificar();
  const { valores: filtros, cambiar: cambiarFiltro } = useFormulario({
    nombre: '',
    categoria: '',
    estadoActivo: '',
    ordenStock: '',
  });
  const [enEdicion, setEnEdicion] = useState(null);
  const [aDesactivar, setADesactivar] = useState(null);
  const [idOcupado, setIdOcupado] = useState(null);
  const cargarProductos = useCallback(() => listarProductos(filtros), [filtros]);
  const { datos, error, recargar } = useCarga(cargarProductos);
  const productos = datos || [];

  async function activar(producto) {
    setIdOcupado(producto.id);
    try {
      await activarProducto(producto.id);
      notificar(`${producto.nombre} activado.`);
      recargar();
    } catch (excepcion) {
      notificar(obtenerMensajeDeError(excepcion), true);
    } finally {
      setIdOcupado(null);
    }
  }

  function alternarActivo(producto) {
    if (producto.estadoActivo) {
      setADesactivar(producto);
    } else {
      activar(producto);
    }
  }

  async function desactivar() {
    await darDeBajaProducto(aDesactivar.id);
    notificar(`${aDesactivar.nombre} desactivado.`);
    recargar();
  }

  async function guardar(datosProducto) {
    const esNuevo = !enEdicion.id;
    const guardado = esNuevo
      ? await crearProducto(datosProducto)
      : await modificarProducto(enEdicion.id, datosProducto);
    notificar(`${guardado.nombre} ${esNuevo ? 'creado' : 'actualizado'}.`);
    setEnEdicion(null);
    recargar();
  }

  return (
    <>
      <div className="pagina-cabecera">
        <h1>Productos</h1>
        <button type="button" className="btn btn-primario" onClick={() => setEnEdicion({})}>
          Nuevo producto
        </button>
      </div>

      <div className="filtros">
        <input
          type="search"
          className="buscador"
          placeholder="Buscar por nombre"
          aria-label="Buscar por nombre"
          value={filtros.nombre}
          onChange={cambiarFiltro('nombre')}
        />
        <SelectCategoria
          valor={filtros.categoria}
          alCambiar={cambiarFiltro('categoria')}
          textoTodas="Todas las categorías"
        />
        <select aria-label="Estado" value={filtros.estadoActivo} onChange={cambiarFiltro('estadoActivo')}>
          <option value="">Activos y desactivados</option>
          <option value="true">Solo activos</option>
          <option value="false">Solo desactivados</option>
        </select>
        <select aria-label="Orden" value={filtros.ordenStock} onChange={cambiarFiltro('ordenStock')}>
          <option value="">Sin ordenar</option>
          <option value={ORDEN_STOCK.ASCENDENTE}>Menos stock primero</option>
          <option value={ORDEN_STOCK.DESCENDENTE}>Más stock primero</option>
        </select>
      </div>

      <Aviso error={error} alReintentar={recargar} />
      {datos && productos.length === 0 && <p className="estado-vacio">No hay productos con estos filtros.</p>}
      {productos.length > 0 && (
        <div className="tabla-contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Categoría</th>
                <th className="numero">Precio</th>
                <th className="numero">Stock</th>
                <th>Estado</th>
                <th aria-label="Acciones" />
              </tr>
            </thead>
            <tbody>
              {productos.map((producto) => (
                <tr key={producto.id} className={producto.estadoActivo ? '' : 'fila-apagada'}>
                  <td>
                    <strong>{producto.nombre}</strong>
                    {producto.descripcion && <small className="subtexto">{producto.descripcion}</small>}
                  </td>
                  <td>{nombrarCategoria(producto.categoria)}</td>
                  <td className="numero">{formatearPesos(producto.precio)}</td>
                  <td className={`numero ${producto.stock <= STOCK_BAJO ? 'stock-bajo' : ''}`}>{producto.stock}</td>
                  <td>
                    <span className={`chip ${producto.estadoActivo ? 'chip-ok' : 'chip-apagado'}`}>
                      {producto.estadoActivo ? 'Activo' : 'Desactivado'}
                    </span>
                  </td>
                  <td className="acciones">
                    <button
                      type="button"
                      className="btn btn-ghost btn-chico"
                      disabled={!producto.estadoActivo}
                      title={producto.estadoActivo ? undefined : 'Activalo para poder editarlo.'}
                      onClick={() => setEnEdicion(producto)}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className={`btn btn-ghost btn-chico ${producto.estadoActivo ? 'texto-peligro' : ''}`}
                      disabled={idOcupado === producto.id}
                      onClick={() => alternarActivo(producto)}
                    >
                      {producto.estadoActivo ? 'Desactivar' : 'Activar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {enEdicion && <FormularioProducto producto={enEdicion} alGuardar={guardar} alCerrar={() => setEnEdicion(null)} />}
      {aDesactivar && (
        <DialogoConfirmacion
          titulo={`Desactivar ${aDesactivar.nombre}`}
          texto="Deja de aparecer al cargar pedidos. Podés volver a activarlo cuando quieras."
          textoAccion="Desactivar"
          alConfirmar={desactivar}
          alCerrar={() => setADesactivar(null)}
        />
      )}
    </>
  );
}
