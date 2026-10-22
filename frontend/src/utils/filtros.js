export function quitarVacios(parametros) {
  return Object.fromEntries(Object.entries(parametros).filter(([, valor]) => ![undefined, null, ''].includes(valor)));
}
