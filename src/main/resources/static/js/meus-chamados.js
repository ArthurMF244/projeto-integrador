document.addEventListener('DOMContentLoaded', carregarMeusChamados);

async function carregarMeusChamados() {
  const tbody = $('#listaMeusChamados');
  const empty = $('#emptyState');
  const profile = getCurrentProfile();

  try {
    const params = new URLSearchParams({ solicitante: profile.nome });
    const { data } = await fetchJson(`${API_BASE}/chamados.html?${params}`);
    tbody.innerHTML = '';

    data.forEach((chamado) => {
      const row = document.createElement('tr');
      row.className = 'clickable-row';
      row.tabIndex = 0;
      row.innerHTML = `
        <td><strong>#${chamado.id}</strong></td>
        <td><strong class="called-title">${escapeHtml(chamado.titulo)}</strong></td>
        <td><span class="badge ${badgeClass(chamado.status)}">${escapeHtml(chamado.status)}</span></td>
        <td><span class="badge ${badgeClass(chamado.prioridade)}">${escapeHtml(chamado.prioridade)}</span></td>
        <td>${escapeHtml(chamado.responsavel_nome || '-')}</td>
        <td>${formatDate(chamado.aberto_em)}</td>
      `;
      row.addEventListener('click', () => { window.location.href = `chamado.html?id=${chamado.id}`; });
      row.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') row.click();
      });
      tbody.appendChild(row);
    });

    empty.style.display = data.length ? 'none' : 'block';
  } catch (error) {
    showToast(error.message, 'error');
  }
}
