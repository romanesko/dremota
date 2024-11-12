import {defineConfig} from 'vite'
import {svelte} from '@sveltejs/vite-plugin-svelte'
import path from 'path'


// https://vite.dev/config/
export default defineConfig({
  build: {
    outDir: '../main/resources/static',
    emptyOutDir: true, // also necessary
  },
  plugins: [svelte()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
})
