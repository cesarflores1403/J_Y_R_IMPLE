# J Y R IMPLE

Proyecto full-stack con Node.js/Express en el backend y React en el frontend.

## Estructura del Proyecto

```
J_Y_R_IMPLE/
├── backend/          # API REST con Node.js y Express
│   ├── src/
│   │   ├── controllers/
│   │   ├── models/
│   │   ├── routes/
│   │   ├── middleware/
│   │   ├── config/
│   │   └── index.js
│   ├── package.json
│   ├── .env.example
│   └── README.md
├── frontend/         # Aplicación React
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── hooks/
│   │   ├── styles/
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   ├── .gitignore
│   └── README.md
└── README.md
```

## Primeros Pasos

### Backend

1. Navega a la carpeta `backend`:
   ```bash
   cd backend
   ```

2. Instala las dependencias:
   ```bash
   npm install
   ```

3. Configura las variables de entorno:
   ```bash
   cp .env.example .env
   ```

4. Ejecuta el servidor:
   ```bash
   npm run dev
   ```

El servidor estará disponible en `http://localhost:5000`

### Frontend

1. Navega a la carpeta `frontend`:
   ```bash
   cd frontend
   ```

2. Instala las dependencias:
   ```bash
   npm install
   ```

3. Crea un archivo `.env`:
   ```
   REACT_APP_API_URL=http://localhost:5000
   ```

4. Ejecuta la aplicación:
   ```bash
   npm start
   ```

La aplicación se abrirá en `http://localhost:3000`

## Requisitos

- Node.js (v14 o superior)
- npm o yarn

## Características

- ✅ Backend con Express
- ✅ Frontend con React y React Router
- ✅ Estructura modular y escalable
- ✅ Variables de entorno configurables
- ✅ Ejemplos de componentes y servicios

## Licencia

ISC
