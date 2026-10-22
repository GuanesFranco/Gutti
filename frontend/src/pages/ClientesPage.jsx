import { useState } from 'react';
import { actualizarCliente, listarClientes, obtenerCliente } from '../api/clientes.js';
import Aviso from '../components/Aviso.jsx';
import Encabezado from '../components/Encabezado.jsx';
import FormularioCliente from '../components/FormularioCliente.jsx';
import Modal from '../components/Modal.jsx';
import { useAccionConAviso, useNotificar } from '../components/Notificaciones.jsx';
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
      <Encabezado titulo="Clientes" />
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
                  <td>{cliente.telefono}</td>
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
