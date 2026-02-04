// ============================================================
// API Module - HTTP requests with JWT authentication
// ============================================================
import toast from './toast.js';
const API_BASE = 'http://localhost:8080/api';

const api = {
    getToken() {
        return localStorage.getItem('jyr_token');
    },

    setAuth(data) {
        localStorage.setItem('jyr_token', data.token);
        localStorage.setItem('jyr_user', JSON.stringify({
            id: data.userId,
            username: data.username,
            fullName: data.fullName,
            role: data.role
        }));
    },

    getUser() {
        const u = localStorage.getItem('jyr_user');
        return u ? JSON.parse(u) : null;
    },

    logout() {
        localStorage.removeItem('jyr_token');
        localStorage.removeItem('jyr_user');
        window.location.href = '../index.html';
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    headers(isFormData = false) {
        const h = {};
        if (!isFormData) h['Content-Type'] = 'application/json';
        const token = this.getToken();
        if (token) h['Authorization'] = `Bearer ${token}`;
        return h;
    },

    async request(method, endpoint, body = null, isFormData = false) {
        const url = `${API_BASE}${endpoint}`;
        const options = {
            method,
            headers: this.headers(isFormData),
        };

        if (body) {
            options.body = isFormData ? body : JSON.stringify(body);
        }

        try {
            const res = await fetch(url, options);

            if (res.status === 401) {
                this.logout();
                return;
            }

            if (res.status === 403) {
                toast.error('No tiene permisos para esta acción');
                return null;
            }

            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.message || 'Error en la solicitud');
            }

            return data;
        } catch (err) {
            if (err.message === 'Failed to fetch') {
                toast.error('No se pudo conectar con el servidor');
            }
            throw err;
        }
    },

    get(endpoint) { return this.request('GET', endpoint); },
    post(endpoint, body) { return this.request('POST', endpoint, body); },
    put(endpoint, body) { return this.request('PUT', endpoint, body); },
    patch(endpoint, body) { return this.request('PATCH', endpoint, body); },
    delete(endpoint) { return this.request('DELETE', endpoint); },
    upload(endpoint, formData) { return this.request('POST', endpoint, formData, true); },
};

export default api;
