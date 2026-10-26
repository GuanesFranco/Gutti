import { useState } from 'react';
import useEnvio from '../hooks/useEnvio.js';
import { calcularTotal, cambiarCantidad, itemsDelCarrito, lineasDelCarrito } from '../utils/pedido.js';
import Aviso from './Aviso.jsx';
import DatosEntrega from './DatosEntrega.jsx';
import FiltrosProductos from './FiltrosProductos.jsx';
import LineasPedido from './LineasPedido.jsx';
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
  const { enviar, enviando, error, setError } = useEnvio(avisoInicial);

  const lineas = lineasDelCarrito(carrito);
  const cantidades = Object.fromEntries(lineas.map(({ producto, cantidad }) => [producto.id, cantidad]));

  function sumar(producto, diferencia) {
    setCarrito((actual) => cambiarCantidad(actual, producto, diferencia));
    setError('');
  }

  function cambiarEntrega(campo, valor) {
    setEntrega((anterior) => ({ ...anterior, [campo]: valor }));
    setError('');
  }

  async function confirmar() {
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
            alSumar={(producto) => sumar(producto, 1)}
          />
        )}
      </section>
      <aside className="armado-ticket">
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
            </>
          }
        >
          <LineasPedido lineas={lineas} alCambiarCantidad={sumar} />
        </Ticket>
      </aside>
    </div>
  );
}
