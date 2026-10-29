import { useState } from 'react';
import { actualizarCliente, listarClientes, obtenerCliente } from '../api/clientes.js';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import FormularioCliente from '../components/FormularioCliente.jsx';
import Modal from '../components/Modal.jsx';
import { useNotificar } from '../components/Notificaciones.jsx';
import useAccionConAviso from '../hooks/useAccionConAviso.js';
import useCarga from '../hooks/useCarga.js';

export default function ClientesPage() {
  const notificar = useNotificar();
  const conAviso = useAccionConAviso();
  const { datos: clientes, error, recargar } = useCarga(listarClientes);
  const [enEdicion, setEnEdicion] = useState(null);

  function abrir(id) {
    conAviso(async () => setEnEdicion(await obtenerCliente(id)));
  }

  async function guardar(datos) {
    await actualizarCliente(enEdicion.id, datos);
    notificar('Cliente actualizado.');
    setEnEdicion(null);
    recargar();
  }

  return (
    <>
      <Encabezado
        titulo="Clientes"
        subtitulo="Directorio de clientes registrados y datos de contacto"
      >
        {clientes && <span className="chip chip-ok">{clientes.length} registrados</span>}
      </Encabezado>
      <Aviso error={error} alReintentar={recargar} />
      {clientes?.length === 0 && <p className="estado-vacio">Todavía no hay clientes registrados.</p>}
      {clientes?.length > 0 && (
        <div className="tabla-contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>Cliente</th>
                <th>Teléfono</th>
                <th>Dirección</th>
                <th aria-label="Acciones" />
              </tr>
            </thead>
            <tbody>
              {clientes.map((cliente) => (
                <tr key={cliente.id}>
                  <td>
                    <strong>{cliente.nombre}</strong>
                    <small className="subtexto">{cliente.email}</small>
                  </td>
                  <td>
                    {cliente.telefono ? (
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span>{cliente.telefono}</span>
                        <a
                          href={`https://wa.me/549${cliente.telefono.replace(/\D/g, '')}`}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="btn btn-ghost btn-chico"
                          style={{
                            padding: '2px 8px',
                            fontSize: '0.78rem',
                            color: 'var(--caramelo)',
                            borderColor: 'var(--borde)',
                            display: 'inline-flex',
                            alignItems: 'center',
                          }}
                          title="Contactar al cliente por WhatsApp"
                        >
                          WhatsApp
                        </a>
                      </div>
                    ) : (
                      <span className="texto-suave">—</span>
                    )}
                  </td>
                  <td>{cliente.direccion}</td>
                  <td className="acciones">
                    <button type="button" className="btn btn-ghost btn-chico" onClick={() => abrir(cliente.id)}>
                      Editar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {enEdicion && (
        <Modal titulo={`Editar ${enEdicion.nombre}`} alCerrar={() => setEnEdicion(null)}>
          <FormularioCliente cliente={enEdicion} alGuardar={guardar}>
            <button type="button" className="btn btn-ghost" onClick={() => setEnEdicion(null)}>
              Cancelar
            </button>
          </FormularioCliente>
        </Modal>
      )}
    </>
  );
}
