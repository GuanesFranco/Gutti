import logo from '../assets/logo.jpg';
import { imagenDeProducto } from '../utils/imagenes.js';

export default function FotoProducto({ nombre }) {
  const imagen = imagenDeProducto(nombre);
  return (
    <img className={`producto-foto ${imagen ? '' : 'producto-foto-logo'}`} src={imagen || logo} alt="" loading="lazy" />
  );
}
