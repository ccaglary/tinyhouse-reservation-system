function getToken() { return localStorage.getItem('accessToken'); }
function getUser() { try { return JSON.parse(localStorage.getItem('user')); } catch { return null; } }
function isLoggedIn() { return !!getToken(); }

function logout() {
    const token = getToken();
    if (token) {
        fetch('/api/auth/logout', { method: 'POST', headers: { 'Authorization': 'Bearer ' + token } }).catch(() => {});
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    document.cookie = 'accessToken=; path=/; max-age=0';
    window.location.href = '/login';
}

function renderAuthNav() {
    const nav = document.getElementById('authNav');
    if (!nav) return;

    const user = getUser();
    if (user) {
        let dashLink = '/tenant/profile';
        if (user.role === 'ADMIN') dashLink = '/admin/dashboard';
        else if (user.role === 'OWNER') dashLink = '/owner/dashboard';

        nav.innerHTML = `
            <li class="nav-item dropdown" id="notifDropdownWrapper">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" onclick="loadNotifications()">
                    <i class="bi bi-bell"></i> <span class="badge bg-danger" id="notifBadge" style="display:none">0</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end p-2" id="notifDropdown" style="min-width:320px; max-height:400px; overflow-y:auto;">
                    <li><h6 class="dropdown-header d-flex justify-content-between align-items-center">Bildirimler <a href="javascript:void(0)" onclick="markAllNotificationsAsRead()" class="text-decoration-none text-primary" style="font-size:0.8rem">Tümünü Okundu İşaretle</a></h6></li>
                    <div id="notifList"><li><a class="dropdown-item text-muted text-center py-3" href="#">Yükleniyor...</a></li></div>
                </ul>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                    <i class="bi bi-person-circle"></i> ${user.firstName}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" href="${dashLink}"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
                    <li><a class="dropdown-item" href="/tenant/profile"><i class="bi bi-person me-2"></i>Profil</a></li>
                    <li><a class="dropdown-item" href="/tenant/reservations"><i class="bi bi-calendar me-2"></i>Rezervasyonlarım</a></li>
                    <li><a class="dropdown-item" href="/tenant/favorites"><i class="bi bi-heart me-2"></i>Favorilerim</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="#" onclick="logout()"><i class="bi bi-box-arrow-right me-2"></i>Çıkış Yap</a></li>
                </ul>
            </li>`;
        loadNotificationCount();
    } else {
        nav.innerHTML = `
            <li class="nav-item"><a class="nav-link" href="/login"><i class="bi bi-box-arrow-in-right"></i> Giriş Yap</a></li>
            <li class="nav-item"><a class="nav-link btn btn-primary btn-sm text-white ms-2 px-3" href="/register">Kayıt Ol</a></li>`;
    }
}

async function loadNotificationCount() {
    try {
        const res = await fetch('/api/notifications/unread-count', { headers: { 'Authorization': 'Bearer ' + getToken() } });
        const data = await res.json();
        if (data.success) {
            const badge = document.getElementById('notifBadge');
            if (badge) {
                if (data.data > 0) {
                    badge.textContent = data.data; 
                    badge.style.display = 'inline';
                } else {
                    badge.style.display = 'none';
                }
            }
        }
    } catch (e) {}
}

async function loadNotifications() {
    const list = document.getElementById('notifList');
    if (!list) return;
    
    try {
        list.innerHTML = '<li><a class="dropdown-item text-muted text-center py-3" href="#">Yükleniyor...</a></li>';
        const res = await fetch('/api/notifications?size=10&sort=createdAt,desc', { headers: { 'Authorization': 'Bearer ' + getToken() } });
        const data = await res.json();
        
        if (data.success && data.data.content && data.data.content.length > 0) {
            list.innerHTML = data.data.content.map(n => `
                <li>
                    <a class="dropdown-item text-wrap border-bottom pb-2 pt-2 ${!n.read ? 'bg-light' : ''}" href="javascript:void(0)" onclick="markNotificationAsRead(${n.id})">
                        <div class="d-flex w-100 justify-content-between">
                            <strong class="mb-1">${n.title}</strong>
                            <small class="text-muted" style="font-size:0.75rem">${new Date(n.createdAt).toLocaleDateString('tr-TR')}</small>
                        </div>
                        <p class="mb-0 text-muted" style="font-size:0.85rem">${n.message}</p>
                    </a>
                </li>
            `).join('');
        } else {
            list.innerHTML = '<li><a class="dropdown-item text-muted text-center py-3" href="#">Bildiriminiz yok</a></li>';
        }
    } catch (e) {
        list.innerHTML = '<li><a class="dropdown-item text-danger text-center py-3" href="#">Hata oluştu</a></li>';
    }
}

async function markNotificationAsRead(id) {
    try {
        await fetch(`/api/notifications/${id}/read`, { method: 'PUT', headers: { 'Authorization': 'Bearer ' + getToken() } });
        loadNotificationCount();
        loadNotifications();
    } catch (e) {}
}

async function markAllNotificationsAsRead() {
    try {
        await fetch('/api/notifications/read-all', { method: 'PUT', headers: { 'Authorization': 'Bearer ' + getToken() } });
        loadNotificationCount();
        loadNotifications();
    } catch (e) {}
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const icons = { success: 'check-circle-fill', danger: 'exclamation-circle-fill', warning: 'exclamation-triangle-fill', info: 'info-circle-fill' };
    const toast = document.createElement('div');
    toast.className = `toast show align-items-center text-white bg-${type}`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `<div class="d-flex"><div class="toast-body"><i class="bi bi-${icons[type] || 'info-circle-fill'} me-2"></i>${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

window.addEventListener('scroll', function () {
    const nav = document.getElementById('mainNav');
    if (nav) {
        if (window.scrollY > 50) nav.classList.add('scrolled');
        else nav.classList.remove('scrolled');
    }
});

document.addEventListener('DOMContentLoaded', renderAuthNav);
