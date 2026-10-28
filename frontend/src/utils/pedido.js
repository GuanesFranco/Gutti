import { TIPO_ENTREGA } from './dominio.js';

export function claveLinea(productoId, gustos) {
  return gustos ? `${productoId}__${gustos}` : productoId;
}

// Carrito: { [clave]: { producto, cantidad, gustos? } }.
export function cambiarCantidad(carrito, producto, diferencia, gustos) {
  const clave = claveLinea(producto.id, gustos);
  const actual = carrito[clave];
  const cantidad = (actual?.cantidad || 0) + diferencia;
  const nuevo = { ...carrito };
  if (cantidad > 0) {
    nuevo[clave] = {
      producto,
      cantidad,
      ...(gustos ? { gustos } : (actual?.gustos ? { gustos: actual.gustos } : {})),
    };
  } else {
    delete nuevo[clave];
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
  return lineasDelCarrito(carrito).map(({ producto, cantidad, gustos }) => ({
    productoId: producto.id,
    cantidad,
    ...(gustos ? { gustos } : {}),
  }));
}

// El backend devuelve el nombre del producto, no su id: por eso se busca por nombre.
export function carritoDePedido(pedido, productos) {
  return pedido.items.reduce((carrito, item) => {
    const producto = productos.find((candidato) => candidato.nombre === item.nombreProducto);
    if (!producto) return carrito;
    const clave = claveLinea(producto.id, item.gustos);
    return {
      ...carrito,
      [clave]: {
        producto,
        cantidad: item.cantidad,
        ...(item.gustos ? { gustos: item.gustos } : {}),
      },
    };
  }, {});
}

export function armarEntrega(origen) {
  return {
    tipoDeEntrega: origen?.tipoDeEntrega ?? TIPO_ENTREGA.MOSTRADOR,
    nombreCliente: origen?.nombreCliente ?? '',
    telefono: origen?.telefono ?? '',
    direccion: origen?.direccion ?? '',
  };
}

export function prepararFormulario({ borrador, pedido, perfil, productos }) {
  const datosDelPerfil = perfil && {
    nombreCliente: perfil.nombre,
    telefono: perfil.telefono,
    direccion: perfil.direccion,
  };
  if (borrador) {
    return {
      entrega: armarEntrega(borrador.entrega || datosDelPerfil),
      carrito: borrador.carrito || {},
      faltanProductos: false,
    };
  }
  if (pedido) {
    const carrito = carritoDePedido(pedido, productos);
    const faltanProductos = Object.keys(carrito).length < pedido.items.length;
    return { entrega: armarEntrega(pedido), carrito, faltanProductos };
  }
  return { entrega: armarEntrega(datosDelPerfil), carrito: {}, faltanProductos: false };
}
