import logo from '../assets/logo.jpg';

export default function PantallaAcceso({ titulo, children }) {
  return (
    <div className="acceso">
      <section className="acceso-marca" aria-hidden="true">
        <img className="acceso-logotipo" src={logo} alt="" />
        <p>Churros, medialunas y helados. Pedí por acá y elegí si lo retirás o te lo llevamos.</p>
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
