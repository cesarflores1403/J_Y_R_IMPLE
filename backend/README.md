# Backend - J Y R IMPLE

Backend API construido con Node.js y Express.

## Instalación

```bash
npm install
```

## Variables de entorno

Copia `.env.example` a `.env` y configura las variables:

```bash
cp .env.example .env
```

## Desarrollo

Para ejecutar en modo desarrollo:

```bash
npm run dev
```

## Producción

Para ejecutar en producción:

```bash
npm start
```

## Estructura de carpetas

- `src/` - Código fuente
  - `routes/` - Definición de rutas
  - `controllers/` - Lógica de negocio
  - `models/` - Modelos de datos
  - `middleware/` - Middlewares personalizados
  - `config/` - Configuración de la aplicación
