const MILISEGUNDOS_POR_SEGUNDO = 1000;

export function crearToken(contenido) {
  const codificar = (objeto) => btoa(JSON.stringify(objeto)).replace(/=+$/, '');
  return `${codificar({ alg: 'HS256' })}.${codificar(contenido)}.firma`;
}

export function segundosDesdeAhora(segundos) {
  return Math.floor(Date.now() / MILISEGUNDOS_POR_SEGUNDO) + segundos;
}
