// ============================================================
// Helpers - Formatters and shared utilities
// ============================================================

export function formatCurrency(amount) {
    const n = parseFloat(amount) || 0;
    return 'L ' + n.toLocaleString('es-HN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export function formatDate(dateStr) {
    if (!dateStr) return '-';
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('es-HN', { year: 'numeric', month: 'short', day: 'numeric' });
}

export function formatDateTime(dtStr) {
    if (!dtStr) return '-';
    const d = new Date(dtStr);
    return d.toLocaleDateString('es-HN', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

export function statusBadge(status) {
    const map = {
        PENDIENTE: 'warning', PAGADA: 'success', ANULADA: 'danger', VENCIDA: 'danger',
        SOLICITADA: 'warning', APROBADA: 'info', RECHAZADA: 'danger', COMPLETADA: 'success',
        RECIBIDA: 'success', PARCIAL: 'info', CANCELADA: 'danger'
    };
    return `<span class="badge badge-${map[status] || 'neutral'}">${status}</span>`;
}

export function roleBadge(role) {
    const map = { ADMIN: 'info', CAJERO: 'success', BODEGUERO: 'warning' };
    const names = { ADMIN: 'Administrador', CAJERO: 'Cajero', BODEGUERO: 'Bodeguero' };
    return `<span class="badge badge-${map[role] || 'neutral'}">${names[role] || role}</span>`;
}

export function debounce(fn, delay = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => fn(...args), delay);
    };
}

export function getInitials(name) {
    if (!name) return '?';
    return name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
}

export function hasRole(...roles) {
    const user = JSON.parse(localStorage.getItem('jyr_user') || '{}');
    return roles.includes(user.role);
}
