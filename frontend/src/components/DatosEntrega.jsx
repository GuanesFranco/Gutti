import { TIPO_ENTREGA } from '../utils/dominio.js';
import Campo from './Campo.jsx';
import Segmentos from './Segmentos.jsx';

export default function DatosEntrega({ entrega = {}, esCliente, alCambiar }) {
  const datos = {
    tipoDeEntrega: entrega.tipoDeEntrega ?? TIPO_ENTREGA.MOSTRADOR,
    nombreCliente: entrega.nombreCliente ?? '',
    telefono: entrega.telefono ?? '',
    direccion: entrega.direccion ?? '',
  };
  const esDelivery = datos.tipoDeEntrega === TIPO_ENTREGA.DELIVERY;
  const opciones = [
    { valor: TIPO_ENTREGA.MOSTRADOR, texto: esCliente ? 'Retiro en el local' : 'Mostrador' },
    { valor: TIPO_ENTREGA.DELIVERY, texto: 'Delivery' },
  ];

  return (
    <>
      <Segmentos
        opciones={opciones}
        valor={datos.tipoDeEntrega}
        alCambiar={(valor) => alCambiar('tipoDeEntrega', valor)}
        etiqueta="Tipo de entrega"
        ancho
      />
      <Campo etiqueta="A nombre de">
        <input value={datos.nombreCliente} onChange={(evento) => alCambiar('nombreCliente', evento.target.value)} />
      </Campo>
      <Campo etiqueta="Teléfono">
        <input type="tel" value={datos.telefono} onChange={(evento) => alCambiar('telefono', evento.target.value)} />
      </Campo>
      {esDelivery && (
        <Campo etiqueta="Dirección de entrega">
          <input
            type="text"
            placeholder="Calle, número, piso/depto"
            value={datos.direccion}
            onChange={(evento) => alCambiar('direccion', evento.target.value)}
          />
        </Campo>
      )}

      <div className="aviso-pago-efectivo">
        <div className="aviso-pago-efectivo-texto">
          <strong>Medio de pago: Solo efectivo</strong>
          <span>
            {esDelivery
              ? 'Abonás en efectivo al recibir tu pedido.'
              : 'Abonás en efectivo al retirar tu pedido en el local.'}
          </span>
        </div>
      </div>
    </>
  );
}
