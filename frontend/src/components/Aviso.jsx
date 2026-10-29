export default function Aviso({ error, ok, alReintentar }) {
  if (error) {
    return (
      <div className="aviso aviso-error" role="alert">
        <span>{error}</span>
        {alReintentar && (
          <button type="button" className="btn btn-ghost btn-chico" onClick={alReintentar}>
            Reintentar
          </button>
        )}
      </div>
    );
  }
  return ok ? <div className="aviso aviso-ok">{ok}</div> : null;
}
