import cafeExpreso from '../assets/cafe-expreso.jpg';
import chipa from '../assets/chipa.jpg';
import chocotorta from '../assets/chocotorta.jpg';
import churros from '../assets/churros.jpg';
import churrosRellenos from '../assets/churros-rellenos.jpg';
import heladoDulceDeLeche from '../assets/helado-dulce-de-leche.jpg';
import heladoFrutosRojos from '../assets/helado-frutos-rojos.jpg';
import heladoMenta from '../assets/helado-menta.jpg';
import medialunas from '../assets/medialunas.jpg';
import tostado from '../assets/tostado-de-miga.jpg';

// El backend no guarda fotos: se elige por el nombre, del más específico al más general.
const IMAGENES = [
  { palabra: 'churros rellenos', imagen: churrosRellenos },
  { palabra: 'churros', imagen: churros },
  { palabra: 'medialunas', imagen: medialunas },
  { palabra: 'chipa', imagen: chipa },
  { palabra: 'helado de dulce de leche', imagen: heladoDulceDeLeche },
  { palabra: 'helado de frutos rojos', imagen: heladoFrutosRojos },
  { palabra: 'helado de menta', imagen: heladoMenta },
  { palabra: '1/4', imagen: heladoDulceDeLeche },
  { palabra: '1/2', imagen: heladoFrutosRojos },
  { palabra: '1 kg', imagen: heladoMenta },
  { palabra: 'helado', imagen: heladoDulceDeLeche },
  { palabra: 'cafe', imagen: cafeExpreso },
  { palabra: 'tostado', imagen: tostado },
  { palabra: 'chocotorta', imagen: chocotorta },
];

function sinTildes(texto) {
  return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

export function imagenDeProducto(nombre) {
  const buscado = sinTildes(nombre.toLowerCase());
  return IMAGENES.find(({ palabra }) => buscado.includes(palabra))?.imagen || null;
}
