const VALORES_VACIOS = [undefined, null, ''];

export function quitarVacios(parametros) {
  const conValor = Object.entries(parametros).filter(([, valor]) => !VALORES_VACIOS.includes(valor));
  return Object.fromEntries(conValor);
}
