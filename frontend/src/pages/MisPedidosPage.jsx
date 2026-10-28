import { Link, useNavigate } from 'react-router-dom';
import { listarMisPedidos } from '../api/pedidos.js';
import { listarProductos } from '../api/productos.js';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import TarjetaPedido from '../components/TarjetaPedido.jsx';
import useCarga from '../hooks/useCarga.js';
import { WHATSAPP_LOCAL } from '../utils/dominio.js';
import { carritoDePedido } from '../utils/pedido.js';
import { RUTAS } from '../utils/rutas.js';

const ESQUELETOS = ['sk-1', 'sk-2'];

export default function MisPedidosPage() {
  const navigate = useNavigate();
  const { datos: pedidos, error, recargar } = useCarga(listarMisPedidos);
  const cargando = pedidos === null && !error;

  async function reordenar(pedido) {
    try {
      const productos = await listarProductos({ estadoActivo: true });
      const carrito = carritoDePedido(pedido, productos);
      navigate(RUTAS.NUEVO_PEDIDO, {
        state: {
          borrador: {
            carrito,
            entrega: {
              tipoDeEntrega: pedido.tipoDeEntrega,
              nombreCliente: pedido.nombreCliente,
              telefono: pedido.telefono,
              direccion: pedido.direccion || '',
            },
          },
        },
      });
    } catch {
      navigate(RUTAS.NUEVO_PEDIDO);
    }
  }

  return (
    <>
      <Encabezado
        titulo="Mis pedidos"
        subtitulo="Historial y estado de tus compras para retiro o delivery"
      >
        <Link to={RUTAS.NUEVO_PEDIDO} className="btn btn-primario">
          Hacer un pedido
        </Link>
      </Encabezado>

      <Aviso error={error} alReintentar={recargar} />

      {cargando && (
        <div className="tickets" aria-busy="true" aria-label="Cargando mis pedidos">
          {ESQUELETOS.map((id) => (
            <div key={id} className="esqueleto-tarjeta" style={{ minHeight: '160px' }} />
          ))}
        </div>
      )}

      {pedidos && pedidos.length === 0 && (
        <div className="estado-vacio-card">
          <h2 className="estado-vacio-titulo">Todavía no realizaste ningún pedido</h2>
          <p className="estado-vacio-bajada">
            ¿Tentado con un café de especialidad o pastelería recién horneada?
          </p>
          <Link to={RUTAS.CATALOGO} className="btn btn-primario">
            Explorar el menú
          </Link>
        </div>
      )}

      {pedidos && pedidos.length > 0 && (
        <>
          <div
            style={{
              background: 'var(--superficie)',
              border: '1px solid var(--borde)',
              borderRadius: '12px',
              padding: '0.85rem 1.25rem',
              marginBottom: '1.5rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '1rem',
              flexWrap: 'wrap',
              fontSize: '0.86rem',
              boxShadow: 'var(--sombra-tarjeta)',
            }}
          >
            <div>
              <span className="texto-suave" style={{ lineHeight: 1.4 }}>
                <strong style={{ color: 'var(--texto)', fontWeight: 600 }}>Pago en efectivo</strong> al momento de la entrega o retiro en el local.
              </span>
            </div>
            <a
              href={`https://wa.me/${WHATSAPP_LOCAL.waMe}`}
              target="_blank"
              rel="noopener noreferrer"
              className="btn btn-ghost btn-chico"
              style={{
                color: 'var(--caramelo)',
                borderColor: 'var(--borde)',
                fontWeight: 600,
                fontSize: '0.82rem',
              }}
            >
              WhatsApp del local
            </a>
          </div>

          <div className="tickets">
            {pedidos.map((pedido) => (
              <TarjetaPedido key={pedido.id} pedido={pedido} alReordenar={reordenar} />
            ))}
          </div>
        </>
      )}
    </>
  );
}
