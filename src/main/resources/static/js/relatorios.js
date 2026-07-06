document.addEventListener('DOMContentLoaded', carregarRelatorios);

async function carregarRelatorios() {
  try {
    const { data: chamados } = await fetchJson(`${API_BASE}/chamados`);
    const areas = [...new Set(chamados.map((chamado) => chamado.area_responsavel))].sort();

    $('#listaRelatorios').innerHTML = areas.map((area) => {
      const chamadosArea = chamados.filter((chamado) => chamado.area_responsavel === area);
      const finalizados = chamadosArea.filter((chamado) => chamado.status === 'Finalizado').length;
      return `
        <tr>
          <td><strong>${escapeHtml(area)}</strong></td>
          <td>${chamadosArea.length}</td>
          <td>${chamadosArea.length - finalizados}</td>
          <td>${finalizados}</td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="4">Nenhum chamado cadastrado.</td></tr>';
  } catch (error) {
    showToast(error.message, 'error');
  }
}
