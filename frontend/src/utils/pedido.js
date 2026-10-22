import { TIPO_ENTREGA } from './dominio.js';

// El carrito guarda cada producto elegido con su cantidad: { [id]: { producto, cantidad } }.
export function cambiarCantidad(carrito, producto, diferencia) {
  const cantidad = (carrito[producto.id]?.cantidad || 0) + diferencia;
  const nuevo = { ...carrito };
  if (cantidad > 0) {
    nuevo[producto.id] = { producto, cantidad };
  } else {
    delete nuevo[producto.id];
  }
  return nuevo;
}

export function lineasDelCarrito(carrito) {
  return Object.values(carrito);
}

export function itemsDelCarrito(carrito) {
  return lineasDelCarrito(carrito).map(({ producto, cantidad }) => ({ productoId: producto.id, cantidad }));
}

// El pedido que devuelve el backend nombra los productos, así que se buscan por nombre para editarlo.
export function carritoDePedido(pedido, productos) {
  return pedido.items.reduce((carrito, item) => {
    const producto = productos.find((candidato) => candidato.nombre === item.nombreProducto);
    return producto ? { ...carrito, [producto.id]: { producto, cantidad: item.cantidad } } : carrito;
  }, {});
}

export function armarEntrega(origen) {
  return {
    tipoDeEntrega: origen?.tipoDeEntrega ?? TIPO_ENTREGA.MOSTRADOR,
    nombreCliente: origen?.nombreCliente ?? '',
    telefono: origen?.telefono ?? '',
  };
}
