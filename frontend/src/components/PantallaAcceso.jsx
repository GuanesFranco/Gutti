import logo from '../assets/logo.jpg';

export default function PantallaAcceso({ titulo, children }) {
  return (
    <div className="acceso">
      <section className="acceso-marca" aria-hidden="true">
        <img className="acceso-logotipo" src={logo} alt="" />
        <p>Pedidos, stock y caja del día en un solo lugar.</p>
      </section>
      <section className="acceso-formulario">
        <div className="acceso-caja">
          <h1>{titulo}</h1>
          {children}
        </div>
      </section>
    </div>
  );
}
