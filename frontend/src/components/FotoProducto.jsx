import logo from '../assets/logo.jpg';
import { imagenDeProducto } from '../utils/imagenes.js';

export default function FotoProducto({ nombre, imagenUrl }) {
  const imagen = imagenUrl || imagenDeProducto(nombre);
  return (
    <img
      className={`producto-foto ${imagen ? '' : 'producto-foto-logo'}`}
      src={imagen || logo}
      alt={nombre || ''}
      loading="lazy"
      onError={(e) => {
        e.currentTarget.onerror = null;
        e.currentTarget.src = imagenDeProducto(nombre) || logo;
      }}
    />
  );
}
