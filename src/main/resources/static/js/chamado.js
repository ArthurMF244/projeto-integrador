let chamadoAtual = null;

document.addEventListener('DOMContentLoaded', () => {
  carregarResponsaveis();
  carregarChamado();
  bindEventosChamado();
});

function getChamadoId() {
  return new URLSearchParams(window.location.search).get('id');
}

function bindEventosChamado() {
  $('#btnAbrirEdicao')?.addEventListener('click', () => {
    if (chamadoAtual) {
      $('#edicaoStatus').value = chamadoAtual.status;
      $('#edicaoPrioridade').value = chamadoAtual.prioridade;
      $('#edicaoAreaResponsavel').value = chamadoAtual.area_responsavel;
      $('#edicaoResponsavel').value = chamadoAtual.responsavel_id || '';
    }
    openModal('#modalEdicaoChamado');
  });

  $('#btnFecharModalEdicao')?.addEventListener('click', () => closeModal('#modalEdicaoChamado'));
  $('#btnCancelarEdicao')?.addEventListener('click', () => closeModal('#modalEdicaoChamado'));
  $('#formEdicaoChamado')?.addEventListener('submit', salvarEdicaoChamado);
}

async function carregarResponsaveis() {
  try {
    const { data } = await fetchJson(`${API_BASE}/usuarios?ativos=true`);
    const select = $('#edicaoResponsavel');
    if (!select) return;

    select.innerHTML = '<option value="">Sem responsável</option>';
    data.forEach((usuario) => {
      const option = document.createElement('option');
      option.value = usuario.id;
      option.textContent = `${usuario.nome} - ${usuario.setor}`;
      select.appendChild(option);
    });
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function carregarChamado() {
  const id = getChamadoId();

  if (!id) {
    showToast('Chamado não informado na URL.', 'error');
    return;
  }

  try {
    const { data, movimentacoes } = await fetchJson(`${API_BASE}/chamados/${id}`);
    chamadoAtual = data;

    $('#tituloPaginaChamado').textContent = `Chamado #${data.id}`;
    $('#resumoNumero').textContent = data.id;
    $('#detalheTituloChamado').textContent = data.titulo;
    $('#resumoStatus').textContent = data.status;
    $('#resumoAbertura').textContent = formatDate(data.aberto_em);
    $('#detalheFinalizadoEm').textContent = formatDate(data.finalizado_em);
    $('#resumoPrioridade').textContent = data.prioridade;
    $('#detalheAreaSolicitante').textContent = data.area_solicitante;
    $('#detalheAreaResponsavel').textContent = data.area_responsavel;
    $('#detalheImpacto').textContent = data.impacto;
    $('#detalheSolicitante').textContent = data.solicitante_nome;
    $('#detalheResponsavel').textContent = data.responsavel_nome || 'Sem responsável';
    $('#detalheCategoria').textContent = data.categoria;
    $('#detalheDescricao').textContent = data.descricao;

    renderMovimentacoes(movimentacoes);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderMovimentacoes(movimentacoes) {
  const container = $('#historicoChamado');
  container.innerHTML = '';

  if (!movimentacoes.length) {
    container.innerHTML = '<p>Nenhuma movimentação registrada ainda.</p>';
    return;
  }

  movimentacoes.forEach((mov) => {
    const div = document.createElement('div');
    div.className = 'movement-item';
    div.innerHTML = `
      <strong>${escapeHtml(mov.status)} • ${escapeHtml(mov.prioridade)}</strong>
      <p>${escapeHtml(mov.descricao || 'Movimentação registrada.')}</p>
      <small>${formatDate(mov.criado_em)} | Resp.: ${escapeHtml(mov.responsavel_nome || 'Sem responsável')}</small>
    `;
    container.appendChild(div);
  });
}

async function salvarEdicaoChamado(event) {
  event.preventDefault();

  const id = getChamadoId();
  const responsavel = $('#edicaoResponsavel');

  const payload = {
    titulo: chamadoAtual.titulo,
    solicitante_id: chamadoAtual.solicitante_id,
    solicitante_nome: chamadoAtual.solicitante_nome,
    area_solicitante: chamadoAtual.area_solicitante,
    status: $('#edicaoStatus').value,
    responsavel_id: responsavel.value || null,
    responsavel_nome: responsavel.value ? responsavel.selectedOptions[0]?.textContent?.split(' - ')[0] : null,
    prioridade: $('#edicaoPrioridade').value,
    area_responsavel: $('#edicaoAreaResponsavel').value,
    categoria: chamadoAtual.categoria,
    impacto: chamadoAtual.impacto,
    descricao: chamadoAtual.descricao,
    descricao_movimentacao: $('#edicaoDescricao').value || 'Movimentação registrada.',
  };

  try {
    await fetchJson(`${API_BASE}/chamados/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });

    $('#formEdicaoChamado').reset();
    closeModal('#modalEdicaoChamado');
    showToast('Chamado atualizado com sucesso.');
    carregarChamado();
  } catch (error) {
    showToast(error.message, 'error');
  }
}
