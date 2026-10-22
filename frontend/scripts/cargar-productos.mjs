// Carga el catálogo de ejemplo usando la API. Uso: npm run productos:cargar [-- --desactivar-otros]
import { readFile } from 'node:fs/promises';

const API_URL = process.env.API_URL || 'http://localhost:8080/api/v1';
const EMAIL = process.env.ADMIN_EMAIL || 'admin@gutti.com';
const PASSWORD = process.env.ADMIN_PASSWORD || 'admin123';
const DESACTIVAR_OTROS = process.argv.includes('--desactivar-otros');

let token = '';

async function llamar(metodo, ruta, datos) {
  const encabezados = { 'Content-Type': 'application/json' };
  if (token) {
    encabezados.Authorization = `Bearer ${token}`;
  }
  const respuesta = await fetch(`${API_URL}${ruta}`, {
    method: metodo,
    headers: encabezados,
    body: datos ? JSON.stringify(datos) : undefined,
  });
  if (!respuesta.ok) {
    throw new Error(`${metodo} ${ruta}: ${respuesta.status} ${await respuesta.text()}`);
  }
  const texto = await respuesta.text();
  return texto ? JSON.parse(texto) : null;
}

const mismoNombre = (a, b) => a.trim().toLowerCase() === b.trim().toLowerCase();

const catalogo = JSON.parse(await readFile(new URL('./productos-iniciales.json', import.meta.url), 'utf8'));
({ token } = await llamar('POST', '/auth/iniciar-sesion', { email: EMAIL, password: PASSWORD }));
const existentes = await llamar('GET', '/productos/consultarproductos');

for (const producto of catalogo) {
  const existente = existentes.find((otro) => mismoNombre(otro.nombre, producto.nombre));
  if (!existente) {
    await llamar('POST', '/productos/crearProducto', producto);
    console.log(`${producto.nombre}: creado`);
    continue;
  }
  if (!existente.estadoActivo) {
    await llamar('PATCH', `/productos/activarProducto/${existente.id}`);
  }
  await llamar('PUT', `/productos/modificarProducto/${existente.id}`, producto);
  console.log(`${producto.nombre}: actualizado`);
}

if (DESACTIVAR_OTROS) {
  const sobrantes = existentes.filter(
    (otro) => otro.estadoActivo && !catalogo.some((producto) => mismoNombre(producto.nombre, otro.nombre)),
  );
  for (const sobrante of sobrantes) {
    await llamar('DELETE', `/productos/eliminarProducto/${sobrante.id}`);
    console.log(`${sobrante.nombre}: dado de baja (no está en el catálogo)`);
  }
}
