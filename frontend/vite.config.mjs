import { resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import tailwindcss from "@tailwindcss/vite";
import vue from "@vitejs/plugin-vue";

const frontendRoot = fileURLToPath(new URL(".", import.meta.url));

export default defineConfig({
    root: frontendRoot,
    plugins: [vue(), tailwindcss()],
    server: {
        host: "0.0.0.0",
        port: 5173,
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true
            }
        }
    },
    preview: {
        host: "0.0.0.0",
        port: 4173
    },
    resolve: {
        alias: {
            "@": resolve(frontendRoot, "src")
        }
    }
});
