import { useState } from 'react';
import useEnvio from '../hooks/useEnvio.js';
import { TIPO_ENTREGA } from '../utils/dominio.js';
import { formatearPesos } from '../utils/formato.js';
import { esHelado } from '../utils/helados.js';
import { calcularTotal, cambiarCantidad, itemsDelCarrito, lineasDelCarrito } from '../utils/pedido.js';
import Aviso from './Aviso.jsx';
import DatosEntrega from './DatosEntrega.jsx';
import FiltrosProductos from './FiltrosProductos.jsx';
import LineasPedido from './LineasPedido.jsx';
import ModalGustosHelado from './ModalGustosHelado.jsx';
import SelectorProductos from './SelectorProductos.jsx';
import Ticket, { TotalTicket } from './Ticket.jsx';

// El total es solo para mostrar: el definitivo lo calcula el backend.
export default function FormularioPedido({
  catalogo,
  esCliente,
  textoConfirmar,
  entregaInicial,
  carritoInicial = {},
  avisoInicial = '',
  alGuardar,
}) {
  const [carrito, setCarrito] = useState(carritoInicial);
  const [entrega, setEntrega] = useState(entregaInicial);
  const [heladoSeleccionando, setHeladoSeleccionando] = useState(null);
  const { enviar, enviando, error, setError } = useEnvio(avisoInicial);

  const lineas = lineasDelCarrito(carrito);
  // Totalizamos las cantidades por productoId para los números en los botones
  const cantidades = lineas.reduce((acc, { producto, cantidad }) => {
    acc[producto.id] = (acc[producto.id] || 0) + cantidad;
    return acc;
  }, {});

  function sumar(producto, diferencia, gustos) {
    setCarrito((actual) => cambiarCantidad(actual, producto, diferencia, gustos));
    setError('');
  }

  function alClickSumar(producto) {
    if (esHelado(producto)) {
      setHeladoSeleccionando(producto);
    } else {
      sumar(producto, 1);
    }
  }

  function alClickRestar(producto) {
    if (esHelado(producto)) {
      const lineasProducto = Object.entries(carrito).filter(([_, l]) => l.producto.id === producto.id);
      if (lineasProducto.length > 0) {
        const [_, linea] = lineasProducto[lineasProducto.length - 1];
        sumar(producto, -1, linea.gustos);
      }
    } else {
      sumar(producto, -1);
    }
  }

  function agregarHeladoConGustos(gustos) {
    if (heladoSeleccionando) {
      sumar(heladoSeleccionando, 1, gustos);
      setHeladoSeleccionando(null);
    }
  }

  function cambiarEntrega(campo, valor) {
    setEntrega((anterior) => ({ ...anterior, [campo]: valor }));
    setError('');
  }

  async function confirmar() {
    if (entrega.tipoDeEntrega === TIPO_ENTREGA.DELIVERY && !entrega.direccion?.trim()) {
      setError('Por favor ingresá la dirección de entrega');
      return;
    }
    const pedido = { ...entrega, items: itemsDelCarrito(carrito) };
    if (!(await enviar(() => alGuardar(pedido, { carrito, entrega })))) {
      catalogo.recargar();
    }
  }

  return (
    <div className="armado">
      <section aria-label="Productos">
        <FiltrosProductos filtros={catalogo.filtros} alCambiar={catalogo.cambiarFiltro} />
        <Aviso error={catalogo.error} alReintentar={catalogo.recargar} />
        {catalogo.productos && (
          <SelectorProductos
            productos={catalogo.productos}
            cantidades={cantidades}
            alSumar={alClickSumar}
            alRestar={alClickRestar}
          />
        )}
        {lineas.length > 0 && (
          <a href="#ticket-pedido" className="barra-flotante-carrito">
            <span>Ver mi pedido</span>
            <strong>
              · {lineas.length} {lineas.length === 1 ? 'ítem' : 'ítems'} ({formatearPesos(calcularTotal(carrito))})
            </strong>
          </a>
        )}
      </section>
      <aside className="armado-ticket" id="ticket-pedido">
        <Ticket
          cabecera={<DatosEntrega entrega={entrega} esCliente={esCliente} alCambiar={cambiarEntrega} />}
          pie={
            <>
              <TotalTicket monto={calcularTotal(carrito)} />
              <Aviso error={error} />
              <button
                type="button"
                className="btn btn-primario btn-ancho"
                disabled={enviando || lineas.length === 0}
                onClick={confirmar}
              >
                {enviando ? 'Guardando…' : textoConfirmar}
              </button>
              {esCliente && (
                <p
                  className="texto-suave"
                  style={{
                    fontSize: '0.78rem',
                    textAlign: 'center',
                    margin: '0.35rem 0 0',
                    lineHeight: 1.35,
                  }}
                >
                  Una vez confirmado, cualquier modificación debe coordinarse con el local.
                </p>
              )}
            </>
          }
        >
          <LineasPedido lineas={lineas} alCambiarCantidad={sumar} />
        </Ticket>
      </aside>

      {heladoSeleccionando && (
        <ModalGustosHelado
          producto={heladoSeleccionando}
          alConfirmar={agregarHeladoConGustos}
          alCerrar={() => setHeladoSeleccionando(null)}
        />
      )}
    </div>
  );
}
