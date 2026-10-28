export default function Encabezado({ titulo, subtitulo, children }) {
  return (
    <div className="pagina-cabecera">
      <div className="pagina-cabecera-texto">
        <h1>{titulo}</h1>
        {subtitulo && <p className="pagina-subtitulo">{subtitulo}</p>}
      </div>
      {children}
    </div>
  );
}
