import { useState } from 'react';
import useEnvio from '../hooks/useEnvio.js';
import { cambiarCantidad, itemsDelCarrito, lineasDelCarrito } from '../utils/pedido.js';
import Aviso from './Aviso.jsx';
import DatosEntrega from './DatosEntrega.jsx';
import FiltrosProductos from './FiltrosProductos.jsx';
import LineasPedido from './LineasPedido.jsx';
import SelectorProductos from './SelectorProductos.jsx';
import Ticket from './Ticket.jsx';

// El total, el stock y las validaciones del pedido los resuelve el backend.
export default function FormularioPedido({
  catalogo,
  esCliente,
  editando,
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
    const guardado = await enviar(() => alGuardar({ ...entrega, items: itemsDelCarrito(carrito) }));
    if (!guardado) {
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
              <p className="texto-suave">El total lo calcula el sistema al confirmar.</p>
              <Aviso error={error} />
              <button
                type="button"
                className="btn btn-primario btn-ancho"
                disabled={enviando || lineas.length === 0}
                onClick={confirmar}
              >
                {enviando ? 'Guardando…' : editando ? 'Guardar cambios' : 'Confirmar pedido'}
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
