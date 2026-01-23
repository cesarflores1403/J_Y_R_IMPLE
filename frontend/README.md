# Frontend - J Y R IMPLE

Frontend construido con React.

## Instalación

```bash
npm install
```

## Variables de entorno

Crea un archivo `.env` en la raíz del frontend:

```
REACT_APP_API_URL=http://localhost:5000
```

## Desarrollo

Para ejecutar en modo desarrollo:

```bash
npm start
```

La aplicación se abrirá en [http://localhost:3000](http://localhost:3000).

## Construcción para producción

```bash
npm run build
```

## Estructura de carpetas

- `public/` - Archivos estáticos
- `src/` - Código fuente
  - `components/` - Componentes reutilizables
  - `pages/` - Páginas de la aplicación
  - `hooks/` - Custom hooks
  - `services/` - Servicios (llamadas a API)
  - `styles/` - Estilos CSS
