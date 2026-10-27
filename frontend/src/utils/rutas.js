export const RUTAS = {
  INICIO: '/',
  CATALOGO: '/catalogo',
  LOGIN: '/login',
  REGISTRO: '/registro',
  PEDIDOS: '/pedidos',
  NUEVO_PEDIDO: '/pedidos/nuevo',
  EDITAR_PEDIDO: '/pedidos/:id/editar',
  PRODUCTOS: '/productos',
  CLIENTES: '/clientes',
  PERFIL: '/perfil',
};

export function rutaEditarPedido(id) {
  return RUTAS.EDITAR_PEDIDO.replace(':id', id);
}
