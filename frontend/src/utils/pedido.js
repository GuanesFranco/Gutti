import { TIPO_ENTREGA } from './dominio.js';

export function ajustarCantidad(actual, diferencia, stock) {
  return Math.max(0, Math.min(actual + diferencia, stock));
}

export function armarLineas(productos, cantidades) {
  return productos
    .filter((producto) => cantidades[producto.id] > 0)
    .map((producto) => ({ producto, cantidad: cantidades[producto.id] }));
}

export function calcularTotal(lineas) {
  return lineas.reduce((suma, linea) => {
    const subtotal = linea.producto.precio * linea.cantidad;
    return suma + subtotal;
  }, 0);
}

export function cantidadesDePedido(pedido, productos) {
  const cantidades = {};
  pedido.items.forEach((item) => {
    const producto = productos.find((candidato) => candidato.nombre === item.nombreProducto);
    if (producto) {
      cantidades[producto.id] = item.cantidad;
    }
  });
  return cantidades;
}

// Al editar, el stock que ya tenía reservado el pedido vuelve a estar disponible.
export function sumarStockReservado(productos, cantidades) {
  return productos.map((producto) => ({ ...producto, stock: producto.stock + (cantidades[producto.id] || 0) }));
}

export function armarEntrega(origen) {
  return {
    tipoDeEntrega: origen?.tipoDeEntrega ?? TIPO_ENTREGA.MOSTRADOR,
    nombreCliente: origen?.nombreCliente ?? '',
    telefono: origen?.telefono ?? '',
  };
}
