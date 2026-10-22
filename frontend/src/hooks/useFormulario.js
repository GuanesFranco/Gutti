import { useState } from 'react';

export default function useFormulario(valoresIniciales) {
  const [valores, setValores] = useState(valoresIniciales);

  function cambiar(campo) {
    return (evento) => {
      const valor = evento.target.value;
      setValores((anteriores) => ({ ...anteriores, [campo]: valor }));
    };
  }

  return { valores, cambiar };
}
