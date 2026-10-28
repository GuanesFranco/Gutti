export const GUSTOS_HELADO = [
  'Dulce de leche con brownie',
  'Dulce de leche clásico',
  'Dulce de leche granizado',
  'Chocolate amargo',
  'Chocolate con almendras',
  'Chocolate blanco',
  'Frutos rojos',
  'Menta granizada',
  'Tramontana',
  'Frutilla a la crema',
  'Vainilla natural',
  'Americana',
  'Sambayón con nueces',
  'Limón al agua',
];

export const FAMILIAS_GUSTOS = [
  {
    titulo: 'Dulces de leche & Chocolates',
    gustos: [
      'Dulce de leche con brownie',
      'Dulce de leche clásico',
      'Dulce de leche granizado',
      'Chocolate amargo',
      'Chocolate con almendras',
      'Chocolate blanco',
    ],
  },
  {
    titulo: 'Cremas clásicas & especiales',
    gustos: [
      'Frutos rojos',
      'Menta granizada',
      'Tramontana',
      'Frutilla a la crema',
      'Vainilla natural',
      'Americana',
      'Sambayón con nueces',
    ],
  },
  {
    titulo: 'Frutales & al agua',
    gustos: [
      'Limón al agua',
    ],
  },
];

export function esHelado(producto) {
  if (!producto) return false;
  return producto.categoria === 'HELADOS' || producto.nombre?.toLowerCase().includes('helado');
}

export function maxGustosPermitidos(producto) {
  if (!producto) return 2;
  const nombre = producto.nombre?.toLowerCase() || '';
  if (nombre.includes('1 kg') || nombre.includes('1kg')) return 4;
  if (nombre.includes('1/2') || nombre.includes('medio')) return 3;
  if (nombre.includes('1/4') || nombre.includes('cuarto')) return 2;
  return 2;
}

