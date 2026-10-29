// @vitest-environment jsdom
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, describe, expect, it } from 'vitest';
import { guardarRol, guardarToken } from '../src/api/token.js';
import { AuthProvider } from '../src/auth/AuthContext.jsx';
import RutaSinSesion from '../src/auth/RutaSinSesion.jsx';
import { ROL } from '../src/utils/dominio.js';
import { crearToken, segundosDesdeAhora } from './ayudantes.js';

const SEGUNDOS_POR_HORA = 3600;

function mostrarLogin(estado) {
  render(
    <AuthProvider>
      <MemoryRouter initialEntries={[{ pathname: '/login', state: estado }]}>
        <Routes>
          <Route
            path="/login"
            element={
              <RutaSinSesion>
                <p>Pantalla de login</p>
              </RutaSinSesion>
            }
          />
          <Route path="/pedidos/nuevo" element={<p>Pedido armado</p>} />
          <Route path="/" element={<p>Pantalla de inicio</p>} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
}

describe('RutaSinSesion', () => {
  afterEach(() => {
    cleanup();
    sessionStorage.clear();
  });

  it('muestra el login a quien no ingresó', () => {
    mostrarLogin();
    expect(screen.getByText('Pantalla de login')).toBeTruthy();
  });

  it('después de ingresar vuelve al pedido que se estaba armando', () => {
    guardarToken(crearToken({ sub: 'usuario@example.com', exp: segundosDesdeAhora(SEGUNDOS_POR_HORA) }));
    guardarRol(ROL.CLIENTE);
    mostrarLogin({ volverA: { ruta: '/pedidos/nuevo' } });
    expect(screen.getByText('Pedido armado')).toBeTruthy();
  });

  it('sin destino guardado va al inicio', () => {
    guardarToken(crearToken({ sub: 'usuario@example.com', exp: segundosDesdeAhora(SEGUNDOS_POR_HORA) }));
    guardarRol(ROL.CLIENTE);
    mostrarLogin();
    expect(screen.getByText('Pantalla de inicio')).toBeTruthy();
  });
});
