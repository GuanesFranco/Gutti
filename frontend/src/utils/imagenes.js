import chipa from '../assets/chipa.jpg';
import churros from '../assets/churros.jpg';
import churrosRellenos from '../assets/churros-rellenos.jpg';
import heladoDulceDeLeche from '../assets/helado-dulce-de-leche.jpg';
import heladoFrutosRojos from '../assets/helado-frutos-rojos.jpg';
import heladoMenta from '../assets/helado-menta.jpg';
import medialunas from '../assets/medialunas.jpg';

// El backend no guarda fotos: se elige según el nombre del producto, de lo más específico a lo más general.
const IMAGENES = [
  { palabra: 'churros rellenos', imagen: churrosRellenos },
  { palabra: 'churros', imagen: churros },
  { palabra: 'medialunas', imagen: medialunas },
  { palabra: 'chipa', imagen: chipa },
  { palabra: 'helado de dulce de leche', imagen: heladoDulceDeLeche },
  { palabra: 'helado de frutos rojos', imagen: heladoFrutosRojos },
  { palabra: 'helado de menta', imagen: heladoMenta },
];

function sinTildes(texto) {
  return texto.normalize('NFD').replace(/[̀-ͯ]/g, '');
}

export function imagenDeProducto(nombre) {
  const buscado = sinTildes(nombre.toLowerCase());
  return IMAGENES.find(({ palabra }) => buscado.includes(palabra))?.imagen || null;
}
