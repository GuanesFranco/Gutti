import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// En desarrollo, /api se reenvía al backend.
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, strictPort: true, proxy: { '/api': 'http://localhost:8080' } },
});
