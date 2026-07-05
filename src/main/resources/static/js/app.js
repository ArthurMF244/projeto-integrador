const API_BASE = '/api';

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function formatDate(value) {
  if (!value) return '-';
  const date = new Date(String(value).replace(' ', 'T'));
  return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString('pt-BR');
}

function badgeClass(value) {
  const classes = {
    Novo: 'novo',
    'Em atendimento': 'atendimento',
    'Aguardando retorno': 'aguardando',
    Finalizado: 'finalizado',
    Baixa: 'baixa',
    Média: 'media',
    Alta: 'alta',
    Crítica: 'critica',
  };
  return classes[value] || '';
}

async function fetchJson(url, options = {}) {
  const headers = {
    Accept: 'application/json',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {}),
  };

  const response = await fetch(url, { ...options, headers });
  const payload = await response.json().catch(() => ({}));

  if (!response.ok) {
    const detalhes = payload.campos ? Object.values(payload.campos).join(' ') : '';
    throw new Error(detalhes || payload.mensagem || payload.erro || `Erro na requisição (${response.status}).`);
  }

  return payload;
}

function showToast(message, type = 'success') {
  const toast = $('#toast');
  if (!toast) {
    window.alert(message);
    return;
  }

  toast.textContent = message;
  toast.dataset.type = type;
  toast.style.display = 'block';
  toast.classList.add('show');

  window.setTimeout(() => {
    toast.classList.remove('show');
    toast.style.display = 'none';
  }, 3000);
}

function openModal(selector) {
  const modal = $(selector);
  if (!modal) return;
  modal.setAttribute('aria-hidden', 'false');
  modal.style.removeProperty('display');
  modal.classList.add('show', 'active', 'open');
}

function closeModal(selector) {
  const modal = $(selector);
  if (!modal) return;
  modal.setAttribute('aria-hidden', 'true');
  modal.classList.remove('show', 'active', 'open');
  modal.style.removeProperty('display');
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme || '');
}

function getCurrentProfile() {
  const fallback = {
    nome: 'Arthur de Marco Faggion',
    email: 'arthur@example.com',
    setor: 'TI - Sistemas',
  };

  try {
    return { ...fallback, ...JSON.parse(localStorage.getItem('si_perfil_usuario') || '{}') };
  } catch {
    return fallback;
  }
}

function currentPage() {
  return window.location.pathname.split('/').pop() || 'index.html';
}

function activeClass(pages) {
  return pages.includes(currentPage()) ? 'active' : '';
}

function renderSidebar() {
  const sidebar = $('#sidebarContainer');
  if (!sidebar) return;

  const profile = getCurrentProfile();
  const initials = profile.nome
    .split(/\s+/)
    .filter(Boolean)
    .filter((_, index, parts) => index === 0 || index === parts.length - 1)
    .map((part) => part[0])
    .join('')
    .toUpperCase();
  const reportsActive = ['indicadores.html', 'relatorios.html'].includes(currentPage());
  const collapsed = localStorage.getItem('si_sidebar_comprimida') === 'true';

  document.body.classList.toggle('sidebar-collapsed', collapsed);
  sidebar.innerHTML = `
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-main">
          <div class="brand-icon">SI</div>
          <div class="brand-text">
            <strong class="brand-system-name">SI Chamados</strong>
            <span>Sistema interno</span>
          </div>
        </div>
        <button class="sidebar-toggle" id="btnToggleSidebar" type="button" aria-label="${collapsed ? 'Expandir menu' : 'Comprimir menu'}">
          <i class="fa-solid ${collapsed ? 'fa-chevron-right' : 'fa-chevron-left'}"></i>
        </button>
      </div>

      <nav class="menu" aria-label="Menu principal">
        <a class="menu-link ${activeClass(['index.html', 'chamado.html'])}" href="index.html" title="Chamados">
          <i class="fa-solid fa-ticket"></i><span class="menu-label">Chamados</span>
        </a>
        <a class="menu-link ${activeClass(['atribuidos.html'])}" href="atribuidos.html" title="Chamados atribuídos a mim">
          <i class="fa-solid fa-user-check"></i><span class="menu-label">Atribuídos a mim</span>
        </a>
        <a class="menu-link ${activeClass(['meus-chamados.html'])}" href="meus-chamados.html" title="Meus chamados">
          <i class="fa-solid fa-inbox"></i><span class="menu-label">Meus chamados</span>
        </a>
        <a class="menu-link ${activeClass(['usuarios.html'])}" href="usuarios.html" title="Usuários">
          <i class="fa-solid fa-users"></i><span class="menu-label">Usuários</span>
        </a>
        <div class="menu-group ${reportsActive ? 'open' : ''}">
          <button class="menu-parent ${reportsActive ? 'active' : ''}" id="btnRelatoriosMenu" type="button" aria-expanded="${reportsActive}">
            <i class="fa-solid fa-chart-pie"></i><span class="menu-label">Relatórios</span><i class="fa-solid fa-chevron-down submenu-arrow"></i>
          </button>
          <div class="submenu">
            <a class="${activeClass(['indicadores.html'])}" href="indicadores.html">Indicadores</a>
            <a class="${activeClass(['relatorios.html'])}" href="relatorios.html">Relatórios</a>
          </div>
        </div>
        <a class="menu-link ${activeClass(['configuracoes.html'])}" href="configuracoes.html" title="Configurações">
          <i class="fa-solid fa-gear"></i><span class="menu-label">Configurações</span>
        </a>
      </nav>

      <div class="sidebar-user" title="Perfil local de demonstração">
        <div class="user-avatar">${escapeHtml(initials || 'U')}</div>
        <div class="user-info"><strong>${escapeHtml(profile.nome)}</strong><span>${escapeHtml(profile.email)}</span></div>
      </div>
    </aside>
  `;

  $('#btnToggleSidebar')?.addEventListener('click', () => {
    const nextState = !document.body.classList.contains('sidebar-collapsed');
    document.body.classList.toggle('sidebar-collapsed', nextState);
    localStorage.setItem('si_sidebar_comprimida', String(nextState));
    renderSidebar();
  });

  $('#btnRelatoriosMenu')?.addEventListener('click', (event) => {
    const group = event.currentTarget.closest('.menu-group');
    const isOpen = group.classList.toggle('open');
    event.currentTarget.setAttribute('aria-expanded', String(isOpen));
  });
}

async function loadSystemConfig() {
  try {
    const { data } = await fetchJson(`${API_BASE}/configuracoes.html`);
    if (!data) return;
    $$('.brand-system-name').forEach((element) => {
      element.textContent = data.nome_sistema || 'SI Chamados';
    });
    applyTheme(data.tema || '');
  } catch (error) {
    console.warn(error.message);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  renderSidebar();
  loadSystemConfig();
});
