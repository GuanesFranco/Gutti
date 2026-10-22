const LOCALE = 'es-AR';
const DECIMALES_MAXIMOS = 2;
const MILISEGUNDOS_POR_MINUTO = 60000;
const LARGO_FECHA_ISO = 10;

const FORMATO_PESOS = new Intl.NumberFormat(LOCALE, {
  style: 'currency',
  currency: 'ARS',
  minimumFractionDigits: 0,
  maximumFractionDigits: DECIMALES_MAXIMOS,
});

export function formatearPesos(monto) {
  return FORMATO_PESOS.format(Number(monto) || 0);
}

export function formatearFechaHora(fecha) {
  return new Date(fecha).toLocaleString(LOCALE, {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

// Fecha local en formato yyyy-MM-dd (toISOString la daría en hora UTC).
export function obtenerFechaDeHoy(ahora = new Date()) {
  const desfase = ahora.getTimezoneOffset() * MILISEGUNDOS_POR_MINUTO;
  const fechaLocal = new Date(ahora.getTime() - desfase);
  return fechaLocal.toISOString().slice(0, LARGO_FECHA_ISO);
}
