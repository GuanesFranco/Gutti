import { TIPO_ENTREGA } from './dominio.js';

// Carrito: { [id del producto]: { producto, cantidad } }.
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

export function calcularTotal(carrito) {
  return lineasDelCarrito(carrito).reduce((total, { producto, cantidad }) => total + producto.precio * cantidad, 0);
}

export function itemsDelCarrito(carrito) {
  return lineasDelCarrito(carrito).map(({ producto, cantidad }) => ({ productoId: producto.id, cantidad }));
}

// El backend devuelve el nombre del producto, no su id: por eso se busca por nombre.
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

export function prepararFormulario({ borrador, pedido, perfil, productos }) {
  if (borrador) {
    return { entrega: borrador.entrega, carrito: borrador.carrito, faltanProductos: false };
  }
  if (pedido) {
    const carrito = carritoDePedido(pedido, productos);
    const faltanProductos = Object.keys(carrito).length < pedido.items.length;
    return { entrega: armarEntrega(pedido), carrito, faltanProductos };
  }
  const datosDelPerfil = perfil && { nombreCliente: perfil.nombre, telefono: perfil.telefono };
  return { entrega: armarEntrega(datosDelPerfil), carrito: {}, faltanProductos: false };
}
