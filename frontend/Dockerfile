# Etapa 1: Build de la SPA con Vite
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Etapa 2: Servidor web de producción con Nginx
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html

# URL del backend por defecto
ENV BACKEND_URL=https://gutti.onrender.com

EXPOSE 80

CMD ["/bin/sh", "-c", "export TARGET_URL=${backend_url:-${BACKEND_URL:-https://gutti.onrender.com}}; export BACKEND_URL=$(echo \"$TARGET_URL\" | sed 's|/*$||'); envsubst '$BACKEND_URL' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
