export const ROL = { ADMIN: 'ADMIN', CLIENTE: 'CLIENTE' };

export const TIPO_ENTREGA = { MOSTRADOR: 'MOSTRADOR', DELIVERY: 'DELIVERY' };

export const ORDEN_STOCK = { ASCENDENTE: 'ASC', DESCENDENTE: 'DESC' };

export const ESTADO_PEDIDO = { PENDIENTE: 'PENDIENTE', ENTREGADO: 'ENTREGADO' };

export const WHATSAPP_LOCAL = {
  numero: '+54 9 11 3838-4644',
  waMe: '5491138384644',
};

export function linkWhatsAppPedido(pedidoId) {
  const texto = encodeURIComponent(`Hola Gutti! Quisiera consultar sobre mi pedido #${pedidoId}.`);
  return `https://wa.me/${WHATSAPP_LOCAL.waMe}?text=${texto}`;
}

export const CATEGORIAS = {
  INFUSIONES: 'Infusiones',
  BEBIDAS: 'Bebidas',
  PANADERIA: 'Panadería',
  TORTAS: 'Tortas',
  SANGUCHES: 'Sánguches',
  SALADOS: 'Salados',
  HELADOS: 'Helados',
  PROMOS: 'Promociones',
};

export const OPCIONES_CATEGORIA = [
  { valor: '', texto: 'Todo' },
  ...Object.entries(CATEGORIAS).map(([valor, texto]) => ({ valor, texto })),
];

export function nombrarCategoria(categoria) {
  return CATEGORIAS[categoria] || categoria;
}
