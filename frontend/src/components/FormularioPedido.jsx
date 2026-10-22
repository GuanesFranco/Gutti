import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { crearPedido, modificarPedido } from '../api/pedidos.js';
import { useAuth } from '../auth/AuthContext.jsx';
import { ROL } from '../utils/dominio.js';
import { obtenerMensajeDeError } from '../utils/errores.js';
import { formatearPesos } from '../utils/formato.js';
import { ajustarCantidad, armarLineas, calcularTotal } from '../utils/pedido.js';
import { RUTAS } from '../utils/rutas.js';
import { validarEntrega } from '../utils/validaciones.js';
import Aviso from './Aviso.jsx';
import DatosEntrega from './DatosEntrega.jsx';
import LineasPedido from './LineasPedido.jsx';
import { useNotificar } from './Notificaciones.jsx';
import PedidoConfirmado from './PedidoConfirmado.jsx';
import SelectorProductos from './SelectorProductos.jsx';
import Ticket, { TotalTicket } from './Ticket.jsx';

// Sirve para crear o editar un pedido. El total y el stock los recalcula el backend.
export default function FormularioPedido({ productos, pedido, entregaInicial, cantidadesIniciales = {}, alRecargar }) {
  const esCliente = useAuth().rol === ROL.CLIENTE;
  const navigate = useNavigate();
  const notificar = useNotificar();
  const [cantidades, setCantidades] = useState(cantidadesIniciales);
  const [entrega, setEntrega] = useState(entregaInicial);
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [confirmado, setConfirmado] = useState(null);

  const lineas = armarLineas(productos, cantidades);

  function cambiarCantidad(producto, diferencia) {
    setCantidades((actuales) => ({
      ...actuales,
      [producto.id]: ajustarCantidad(actuales[producto.id] || 0, diferencia, producto.stock),
    }));
  }

  function cambiarEntrega(campo, valor) {
    setEntrega((anterior) => ({ ...anterior, [campo]: valor }));
    setError('');
  }

  async function enviar() {
    const errorValidacion = validarEntrega(entrega);
    setError(errorValidacion);
    if (errorValidacion) {
      return;
    }
    setEnviando(true);
    try {
      const items = lineas.map((linea) => ({ productoId: linea.producto.id, cantidad: linea.cantidad }));
      const datos = { ...entrega, items };
      const guardado = pedido ? await modificarPedido(pedido.id, datos) : await crearPedido(datos);
      if (esCliente) {
        setConfirmado(guardado);
        return;
      }
      const accion = pedido ? 'actualizado' : 'registrado';
      notificar(`Pedido #${guardado.id} ${accion} por ${formatearPesos(guardado.total)}.`);
      navigate(RUTAS.PEDIDOS);
    } catch (excepcion) {
      setError(obtenerMensajeDeError(excepcion));
      alRecargar();
    } finally {
      setEnviando(false);
    }
  }

  function hacerOtroPedido() {
    setConfirmado(null);
    setCantidades({});
    alRecargar();
  }

  if (confirmado) {
    return <PedidoConfirmado pedido={confirmado} alHacerOtro={hacerOtroPedido} />;
  }

  return (
    <div className="armado">
      <SelectorProductos
        productos={productos}
        cantidades={cantidades}
        alSumar={(producto) => cambiarCantidad(producto, 1)}
      />
      <aside className="armado-ticket">
        <Ticket
          cabecera={<DatosEntrega entrega={entrega} esCliente={esCliente} alCambiar={cambiarEntrega} />}
          pie={
            <>
              <TotalTicket monto={calcularTotal(lineas)} />
              <Aviso error={error} />
              <button
                type="button"
                className="btn btn-primario btn-ancho"
                disabled={enviando || lineas.length === 0}
                onClick={enviar}
              >
                {enviando ? 'Guardando…' : pedido ? 'Guardar cambios' : 'Confirmar pedido'}
              </button>
            </>
          }
        >
          <LineasPedido lineas={lineas} alCambiarCantidad={cambiarCantidad} />
        </Ticket>
      </aside>
    </div>
  );
}
