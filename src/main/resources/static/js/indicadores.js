document.addEventListener('DOMContentLoaded', carregarIndicadores);

async function carregarIndicadores() {
  try {
    const { data: chamados } = await fetchJson(`${API_BASE}/chamados.html`);
    const total = chamados.length;
    const emAndamento = chamados.filter((chamado) => chamado.status !== 'Finalizado').length;
    const finalizados = chamados.filter((chamado) => chamado.status === 'Finalizado').length;
    const altaPrioridade = chamados.filter((chamado) => ['Alta', 'Crítica'].includes(chamado.prioridade)).length;

    $('#cardsIndicadores').innerHTML = [
      ['Total', total],
      ['Em andamento', emAndamento],
      ['Finalizados', finalizados],
      ['Alta prioridade', altaPrioridade],
    ].map(([label, value]) => `
      <article class="indicator-card"><span>${label}</span><strong>${value}</strong></article>
    `).join('');

    const byArea = chamados.reduce((acc, chamado) => {
      acc[chamado.area_responsavel] = (acc[chamado.area_responsavel] || 0) + 1;
      return acc;
    }, {});

    $('#indicadoresArea').innerHTML = Object.entries(byArea).map(([area, count]) => `
      <div class="area-row">
        <div><strong>${escapeHtml(area)}</strong><span>${count} chamado(s)</span></div>
        <span class="badge media">${count}</span>
      </div>
    `).join('') || '<p>Nenhum chamado cadastrado.</p>';
  } catch (error) {
    showToast(error.message, 'error');
  }
}
