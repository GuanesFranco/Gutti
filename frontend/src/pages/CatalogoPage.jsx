import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { listarProductosActivos } from '../api/productos.js';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import FotoProducto from '../components/FotoProducto.jsx';
import Segmentos from '../components/Segmentos.jsx';
import useCarga from '../hooks/useCarga.js';
import { nombrarCategoria, OPCIONES_CATEGORIA } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import { RUTAS } from '../utils/rutas.js';

export default function CatalogoPage() {
  const { estaAutenticado } = useAuth();
  const cargarProductos = useCallback(() => listarProductosActivos(), []);
  const { datos: productos, error, recargar } = useCarga(cargarProductos);
  const [categoria, setCategoria] = useState('');
  const visibles = (productos || []).filter((producto) => !categoria || producto.categoria === categoria);

  return (
    <>
      <div className="pagina-cabecera">
        <h1>Catálogo</h1>
        <Link to={estaAutenticado ? RUTAS.NUEVO_PEDIDO : RUTAS.LOGIN} className="btn btn-primario">
          {estaAutenticado ? 'Hacer un pedido' : 'Ingresar para pedir'}
        </Link>
      </div>
      <div className="filtros">
        <Segmentos opciones={OPCIONES_CATEGORIA} valor={categoria} alCambiar={setCategoria} etiqueta="Categoría" />
      </div>
      <Aviso error={error} alReintentar={recargar} />
      {productos && visibles.length === 0 && <p className="estado-vacio">No hay productos para mostrar.</p>}
      <ul className="grilla-catalogo">
        {visibles.map((producto) => (
          <li key={producto.id} className="tarjeta-catalogo">
            <FotoProducto nombre={producto.nombre} />
            <span className="producto-categoria">{nombrarCategoria(producto.categoria)}</span>
            <h2 className="producto-nombre">{producto.nombre}</h2>
            {producto.descripcion && <p className="texto-suave">{producto.descripcion}</p>}
            <span className="producto-precio">{formatearPesos(producto.precio)}</span>
          </li>
        ))}
      </ul>
    </>
  );
}
