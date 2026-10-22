// @vitest-environment jsdom
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { guardarRol, guardarToken } from '../src/api/token.js';
import { AuthProvider } from '../src/auth/AuthContext.jsx';
import RutaProtegida from '../src/auth/RutaProtegida.jsx';
import { ROL } from '../src/utils/dominio.js';
import { crearToken, segundosDesdeAhora } from './ayudantes.js';

const SEGUNDOS_POR_HORA = 3600;

function iniciarSesionComo(rol) {
  guardarToken(crearToken({ sub: 'prueba@gutti.com', exp: segundosDesdeAhora(SEGUNDOS_POR_HORA) }));
  guardarRol(rol);
}

function mostrarPantallaDeAdmin() {
  render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/pedidos']}>
        <Routes>
          <Route
            path="/pedidos"
            element={
              <RutaProtegida rolRequerido={ROL.ADMIN}>
                <p>Pantalla de admin</p>
              </RutaProtegida>
            }
          />
          <Route path="/login" element={<p>Pantalla de login</p>} />
          <Route path="/" element={<p>Pantalla de inicio</p>} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
}

describe('RutaProtegida', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(cleanup);

  it('manda al login si no hay sesión', () => {
    mostrarPantallaDeAdmin();
    expect(screen.getByText('Pantalla de login')).toBeTruthy();
  });

  it('deja entrar al rol pedido', () => {
    iniciarSesionComo(ROL.ADMIN);
    mostrarPantallaDeAdmin();
    expect(screen.getByText('Pantalla de admin')).toBeTruthy();
  });

  it('manda al inicio si el rol no alcanza', () => {
    iniciarSesionComo(ROL.CLIENTE);
    mostrarPantallaDeAdmin();
    expect(screen.getByText('Pantalla de inicio')).toBeTruthy();
  });
});
