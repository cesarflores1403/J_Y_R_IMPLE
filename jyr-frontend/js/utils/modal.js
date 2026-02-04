// ============================================================
// Modal Manager
// ============================================================

const modal = {
    open(id) {
        const el = document.getElementById(id);
        if (el) {
            el.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    },

    close(id) {
        const el = document.getElementById(id);
        if (el) {
            el.classList.remove('active');
            document.body.style.overflow = '';
        }
    },

    closeAll() {
        document.querySelectorAll('.modal-overlay.active').forEach(el => {
            el.classList.remove('active');
        });
        document.body.style.overflow = '';
    },

    confirm(message, title = 'Confirmar') {
        return new Promise((resolve) => {
            const overlay = document.createElement('div');
            overlay.className = 'modal-overlay active';
            overlay.innerHTML = `
                <div class="modal" style="max-width:400px">
                    <div class="modal-header">
                        <h2>${title}</h2>
                    </div>
                    <div class="modal-body">
                        <p style="color:var(--text-secondary)">${message}</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" id="_confirm_cancel">Cancelar</button>
                        <button class="btn btn-primary" id="_confirm_ok">Confirmar</button>
                    </div>
                </div>
            `;
            document.body.appendChild(overlay);
            document.body.style.overflow = 'hidden';

            overlay.querySelector('#_confirm_ok').onclick = () => {
                overlay.remove();
                document.body.style.overflow = '';
                resolve(true);
            };
            overlay.querySelector('#_confirm_cancel').onclick = () => {
                overlay.remove();
                document.body.style.overflow = '';
                resolve(false);
            };
        });
    }
};

export default modal;
