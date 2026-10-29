export default function Campo({ etiqueta, children }) {
  return (
    <label className="campo">
      <span className="campo-etiqueta">{etiqueta}</span>
      {children}
    </label>
  );
}
