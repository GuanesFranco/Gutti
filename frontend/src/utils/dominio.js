export const ROL = { ADMIN: 'ADMIN', CLIENTE: 'CLIENTE' };

export const TIPO_ENTREGA = { MOSTRADOR: 'MOSTRADOR', DELIVERY: 'DELIVERY' };

export const ORDEN_STOCK = { ASCENDENTE: 'ASC', DESCENDENTE: 'DESC' };

export const ESTADO_PEDIDO = { PENDIENTE: 'PENDIENTE', ENTREGADO: 'ENTREGADO' };

export const CATEGORIAS = { INFUSIONES: 'Infusiones', TORTAS: 'Tortas', SANGUCHES: 'Sánguches', HELADOS: 'Helados' };

export const OPCIONES_CATEGORIA = [
  { valor: '', texto: 'Todo' },
  ...Object.entries(CATEGORIAS).map(([valor, texto]) => ({ valor, texto })),
];

export function nombrarCategoria(categoria) {
  return CATEGORIAS[categoria] || categoria;
}
