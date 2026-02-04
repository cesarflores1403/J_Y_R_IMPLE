// ============================================================
// App Shell - Sidebar + Header + Navigation
// ============================================================

import api from '../utils/api.js';
import { getInitials, hasRole } from '../utils/helpers.js';

export function initShell(activeNav) {
    if (!api.isAuthenticated()) {
        window.location.href = '../index.html';
        return;
    }

    const user = api.getUser();
    renderSidebar(user, activeNav);
    renderHeader(user);
    setupMobileMenu();
}

function renderSidebar(user, active) {
    const nav = buildNav(user.role);
    document.getElementById('appSidebar').innerHTML = `
        <div class="sidebar-header">
            <div class="logo-sm">
             <img src="../img/logo.png" alt="JYR Logo" ">
              </div>
            <div class="brand">JYR <span>Repuestos</span></div>
        </div>
        <nav class="sidebar-nav">
            <div class="nav-section">
                <div class="nav-section-title">Principal</div>
            </div>
            ${nav.map(item => {
                if (item.section) {
                    return `<div class="nav-section"><div class="nav-section-title">${item.section}</div></div>`;
                }
                const isActive = item.id === active ? ' active' : '';
                const badge = item.badge ? `<span class="badge">${item.badge}</span>` : '';
                return `<a href="${item.href}" class="nav-item${isActive}">
                    <span class="nav-icon">${item.icon}</span>
                    <span>${item.label}</span>
                    ${badge}
                </a>`;
            }).join('')}
        </nav>
        <div class="sidebar-footer">
            <div class="user-card" onclick="document.getElementById('userMenu').classList.toggle('active')">
                <div class="user-avatar">${getInitials(user.fullName)}</div>
                <div class="user-info">
                    <div class="name">${user.fullName}</div>
                    <div class="role">${user.role}</div>
                </div>
            </div>
        </div>
    `;
}

function renderHeader(user) {
    const header = document.getElementById('appHeader');
    if (!header) return;
    header.innerHTML = `
        <button class="menu-toggle" id="menuToggle">☰</button>
        <div class="header-search">
            
            <span class="search-icon"><i class="fa fa-search"></i></span>
            <input type="text" placeholder="Buscar productos, clientes, facturas..." id="globalSearch">
        </div>
        <div class="header-actions">
            <button class="header-btn" title="Notificaciones">
                <i class="fa fa-bell"></i><span class="dot"></span>
            </button>
            <button class="header-btn" title="Cerrar sesión" id="logoutBtn"><i class="fa fa-sign-out"></i></button>
        </div>
    `;

    document.getElementById('logoutBtn').addEventListener('click', () => {
        api.logout();
    });
}

function setupMobileMenu() {
    const toggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('appSidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
            overlay.classList.toggle('active');
        });
    }

    if (overlay) {
        overlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            overlay.classList.remove('active');
        });
    }
}

function buildNav(role) {
    const items = [
        { id: 'dashboard', icon: '', label: 'Dashboard', href: 'dashboard.html' },
    ];

    // Ventas section - Admin and Cajero
    if (['ADMIN', 'CAJERO'].includes(role)) {
        items.push({ section: 'Ventas' });
        items.push({ id: 'invoices', icon: '', label: 'Facturas', href: 'invoices.html' });
        items.push({ id: 'quotes', icon: '', label: 'Cotizaciones', href: 'quotes.html' });
        items.push({ id: 'returns', icon: '', label: 'Devoluciones', href: 'returns.html' });
        items.push({ id: 'customers', icon: '', label: 'Clientes', href: 'customers.html' });
    }

    // Inventario section - Admin and Bodeguero
    if (['ADMIN', 'BODEGUERO'].includes(role)) {
        items.push({ section: 'Inventario' });
        items.push({ id: 'products', icon: '', label: 'Productos', href: 'products.html' });
        items.push({ id: 'categories', icon: '', label: 'Categorías', href: 'categories.html' });
        items.push({ id: 'inventory', icon: '', label: 'Movimientos', href: 'inventory.html' });
        items.push({ id: 'purchases', icon: '', label: 'Compras', href: 'purchases.html' });
        items.push({ id: 'suppliers', icon: '', label: 'Proveedores', href: 'suppliers.html' });
    }

    // Cajero also gets read-only products
    if (role === 'CAJERO') {
        items.push({ section: 'Inventario' });
        items.push({ id: 'products', icon: '', label: 'Productos', href: 'products.html' });
    }

    // Bodeguero gets read-only customers
    if (role === 'BODEGUERO') {
        items.push({ section: 'Ventas' });
        items.push({ id: 'customers', icon: '', label: 'Clientes', href: 'customers.html' });
    }

    // Admin only
    if (role === 'ADMIN') {
        items.push({ section: 'Sistema' });
        items.push({ id: 'users', icon: '', label: 'Usuarios', href: 'users.html' });
        items.push({ id: 'reports', icon: '', label: 'Reportes', href: 'reports.html' });
    }

    return items;
}

export default initShell;
