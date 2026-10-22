import { TIPO_ENTREGA } from '../utils/dominio.js';
import Campo from './Campo.jsx';
import Segmentos from './Segmentos.jsx';

export default function DatosEntrega({ entrega, esCliente, alCambiar }) {
  const opciones = [
    { valor: TIPO_ENTREGA.MOSTRADOR, texto: esCliente ? 'Retiro en el local' : 'Mostrador' },
    { valor: TIPO_ENTREGA.DELIVERY, texto: 'Delivery' },
  ];

  return (
    <>
      <Segmentos
        opciones={opciones}
        valor={entrega.tipoDeEntrega}
        alCambiar={(valor) => alCambiar('tipoDeEntrega', valor)}
        etiqueta="Tipo de entrega"
        ancho
      />
      <Campo etiqueta="A nombre de">
        <input value={entrega.nombreCliente} onChange={(evento) => alCambiar('nombreCliente', evento.target.value)} />
      </Campo>
      <Campo etiqueta="Teléfono">
        <input type="tel" value={entrega.telefono} onChange={(evento) => alCambiar('telefono', evento.target.value)} />
      </Campo>
    </>
  );
}
