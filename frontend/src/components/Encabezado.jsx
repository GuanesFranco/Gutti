export default function Encabezado({ titulo, children }) {
  return (
    <div className="pagina-cabecera">
      <h1>{titulo}</h1>
      {children}
    </div>
  );
}
