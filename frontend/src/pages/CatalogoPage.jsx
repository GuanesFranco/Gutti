import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import FiltrosProductos from '../components/FiltrosProductos.jsx';
import FotoProducto from '../components/FotoProducto.jsx';
import useProductosActivos from '../hooks/useProductosActivos.js';
import { nombrarCategoria } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import { RUTAS } from '../utils/rutas.js';

export default function CatalogoPage() {
  const { estaAutenticado } = useAuth();
  const { productos, error, recargar, filtros, cambiarFiltro } = useProductosActivos();

  return (
    <>
      <Encabezado titulo="Catálogo">
        <Link to={estaAutenticado ? RUTAS.NUEVO_PEDIDO : RUTAS.LOGIN} className="btn btn-primario">
          {estaAutenticado ? 'Hacer un pedido' : 'Ingresar para pedir'}
        </Link>
      </Encabezado>
      <FiltrosProductos filtros={filtros} alCambiar={cambiarFiltro} />
      <Aviso error={error} alReintentar={recargar} />
      {productos?.length === 0 && <p className="estado-vacio">No hay productos para mostrar.</p>}
      <ul className="grilla-catalogo">
        {(productos || []).map((producto) => (
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
