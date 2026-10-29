import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Aviso from '../components/Aviso.jsx';
import FiltrosProductos from '../components/FiltrosProductos.jsx';
import FotoProducto from '../components/FotoProducto.jsx';
import ModalGustosHelado from '../components/ModalGustosHelado.jsx';
import useProductosActivos from '../hooks/useProductosActivos.js';
import { nombrarCategoria } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import { esHelado } from '../utils/helados.js';
import { armarEntrega, calcularTotal, cambiarCantidad } from '../utils/pedido.js';
import { RUTAS } from '../utils/rutas.js';

const ESQUELETOS = ['sk-1', 'sk-2', 'sk-3', 'sk-4', 'sk-5', 'sk-6'];

export default function CatalogoPage() {
  const { productos, error, recargar, filtros, cambiarFiltro } = useProductosActivos();
  const cargando = productos === null && !error;
  const navigate = useNavigate();
  const [carrito, setCarrito] = useState({});
  const [modalHelado, setModalHelado] = useState(null);

  function sumar(producto) {
    if (esHelado(producto)) {
      setModalHelado(producto);
    } else {
      setCarrito((actual) => cambiarCantidad(actual, producto, 1));
    }
  }

  function restar(producto) {
    if (esHelado(producto)) {
      const lineasProducto = Object.entries(carrito).filter(([_, l]) => l.producto.id === producto.id);
      if (lineasProducto.length > 0) {
        const [_, linea] = lineasProducto[lineasProducto.length - 1];
        setCarrito((actual) => cambiarCantidad(actual, producto, -1, linea.gustos));
      }
    } else {
      setCarrito((actual) => cambiarCantidad(actual, producto, -1));
    }
  }

  function confirmarGustos(gustos) {
    if (modalHelado) {
      setCarrito((actual) => cambiarCantidad(actual, modalHelado, 1, gustos));
      setModalHelado(null);
    }
  }

  function irAlPedido() {
    navigate(RUTAS.NUEVO_PEDIDO, { state: { borrador: { carrito, entrega: armarEntrega() } } });
  }

  const cantidadTotal = Object.values(carrito).reduce((total, linea) => total + linea.cantidad, 0);
  const totalPesos = calcularTotal(carrito);

  return (
    <>
      <div className="banner-starbucks">
        <div className="banner-starbucks-cuerpo">
          <span className="banner-starbucks-insignia">Churrería & Pastelería artesanal</span>
          <h1 className="banner-starbucks-titulo">Churros recién hechos, medialunas y pastelería del día</h1>
          <p className="banner-starbucks-bajada">
            Elaboración artesanal y fresca todos los días. Disfrutá de nuestros clásicos para retiro en el local o con delivery a domicilio.
          </p>
          <div className="banner-starbucks-acciones">
            <Link to={RUTAS.NUEVO_PEDIDO} className="btn btn-banner">
              Hacer un pedido
            </Link>
          </div>
        </div>
      </div>

      <div className="catalogo-seccion-productos">
        <FiltrosProductos filtros={filtros} alCambiar={cambiarFiltro} />
        <Aviso error={error} alReintentar={recargar} />
      </div>
      {cargando && (
        <ul className="grilla-catalogo" aria-busy="true" aria-label="Cargando productos">
          {ESQUELETOS.map((id) => (
            <li key={id} className="esqueleto-tarjeta" />
          ))}
        </ul>
      )}
      {productos?.length === 0 && <p className="estado-vacio">No hay productos para mostrar.</p>}
      {productos && productos.length > 0 && (
        <ul className="grilla-catalogo" style={cantidadTotal > 0 ? { paddingBottom: '5.5rem' } : undefined}>
          {productos.map((producto) => {
            const cantidad = Object.values(carrito)
              .filter((linea) => linea.producto.id === producto.id)
              .reduce((sum, linea) => sum + linea.cantidad, 0);

            return (
              <li key={producto.id}>
                <article className="tarjeta-catalogo">
                  <div className="producto-foto-wrap">
                    <FotoProducto nombre={producto.nombre} imagenUrl={producto.imagenUrl} />
                    <span className="producto-categoria">{nombrarCategoria(producto.categoria)}</span>
                  </div>
                  <div className="tarjeta-catalogo-cuerpo">
                    <h2 className="producto-nombre">
                      <Link to={RUTAS.NUEVO_PEDIDO} className="tarjeta-catalogo-titulo-enlace">
                        {producto.nombre}
                      </Link>
                    </h2>
                    {producto.descripcion && (
                      <p className="texto-suave tarjeta-catalogo-desc">{producto.descripcion}</p>
                    )}
                    <div className="tarjeta-catalogo-pie">
                      <span className="producto-precio">{formatearPesos(producto.precio)}</span>
                      {cantidad > 0 ? (
                        <div className="catalogo-control-cantidad" aria-label={`Cantidad de ${producto.nombre}`}>
                          <button
                            type="button"
                            className="catalogo-btn-paso"
                            aria-label={`Quitar uno de ${producto.nombre}`}
                            onClick={() => restar(producto)}
                          >
                            −
                          </button>
                          <span className="catalogo-contador" aria-hidden="true">{cantidad}</span>
                          <button
                            type="button"
                            className="catalogo-btn-paso"
                            aria-label={`Sumar uno de ${producto.nombre}`}
                            disabled={cantidad >= producto.stock}
                            onClick={() => sumar(producto)}
                          >
                            +
                          </button>
                        </div>
                      ) : (
                        <button
                          type="button"
                          className="btn-sumar-catalogo"
                          disabled={producto.stock <= 0}
                          onClick={() => sumar(producto)}
                        >
                          {producto.stock <= 0 ? 'Agotado' : '+ Agregar'}
                        </button>
                      )}
                    </div>
                  </div>
                </article>
              </li>
            );
          })}
        </ul>
      )}

      {cantidadTotal > 0 && (
        <aside className="barra-flotante-carrito-global" aria-label="Resumen de tu pedido">
          <div className="barra-flotante-info">
            <span className="barra-flotante-icono" aria-hidden="true">
              <svg
                viewBox="0 0 24 24"
                width="18"
                height="18"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <circle cx="9" cy="21" r="1" />
                <circle cx="20" cy="21" r="1" />
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
              </svg>
            </span>
            <div>
              <strong>{cantidadTotal} {cantidadTotal === 1 ? 'producto' : 'productos'}</strong>
              <span className="barra-flotante-monto">{formatearPesos(totalPesos)}</span>
            </div>
          </div>
          <button type="button" className="btn btn-banner btn-chico" onClick={irAlPedido}>
            Ver pedido y confirmar →
          </button>
        </aside>
      )}

      {modalHelado && (
        <ModalGustosHelado
          producto={modalHelado}
          alConfirmar={confirmarGustos}
          alCerrar={() => setModalHelado(null)}
        />
      )}
    </>
  );
}
