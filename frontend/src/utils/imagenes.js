import chipa from '../assets/chipa.jpg';
import churros from '../assets/churros.jpg';
import churrosRellenos from '../assets/churros-rellenos.jpg';
import helado from '../assets/helado.jpg';
import heladoDulceDeLeche from '../assets/helado-dulce-de-leche.jpg';
import heladoFrutosRojos from '../assets/helado-frutos-rojos.jpg';
import medialunas from '../assets/medialunas.jpg';

// La foto se elige por una palabra del nombre del producto. Las más específicas van primero.
const IMAGENES = [
  { palabra: 'relleno', imagen: churrosRellenos },
  { palabra: 'churro', imagen: churros },
  { palabra: 'frutos rojos', imagen: heladoFrutosRojos },
  { palabra: 'dulce de leche', imagen: heladoDulceDeLeche },
  { palabra: 'menta', imagen: helado },
  { palabra: 'helado', imagen: helado },
  { palabra: 'chipa', imagen: chipa },
  { palabra: 'medialuna', imagen: medialunas },
];

function sinTildes(texto) {
  return texto.normalize('NFD').replace(/[̀-ͯ]/g, '');
}

export function imagenDeProducto(nombre) {
  const buscado = sinTildes(nombre.toLowerCase());
  return IMAGENES.find(({ palabra }) => buscado.includes(palabra))?.imagen || null;
}
