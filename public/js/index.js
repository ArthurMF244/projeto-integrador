document.addEventListener('DOMContentLoaded', () => {
  carregarUsuariosSelect();
  carregarChamados();
  bindEventos();
});

function bindEventos() {
  $('#btnNovoChamado')?.addEventListener('click', () => openModal('#modalChamado'));
  $('#btnFecharModal')?.addEventListener('click', () => closeModal('#modalChamado'));
  $('#btnCancelar')?.addEventListener('click', () => closeModal('#modalChamado'));

  $('#btnFiltros')?.addEventListener('click', () => openModal('#modalFiltros'));
  $('#btnFecharModalFiltros')?.addEventListener('click', () => closeModal('#modalFiltros'));
  $('#btnBuscarFiltros')?.addEventListener('click', () => {
    closeModal('#modalFiltros');
    carregarChamados();
  });
  $('#btnLimparFiltros')?.addEventListener('click', () => {
    $('#filtroTexto').value = '';
    $('#filtroStatus').value = '';
    $('#filtroArea').value = '';
    $('#filtroPrioridade').value = '';
    carregarChamados();
  });

  $('#formChamado')?.addEventListener('submit', salvarChamado);
}

async function carregarUsuariosSelect() {
  try {
    const { data } = await fetchJson(`${API_BASE}/usuarios.php?ativos=1`);
    preencherSelectUsuario('#solicitante', data, 'Selecione');
    preencherSelectUsuario('#responsavel', data, 'Sem responsável', true);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function preencherSelectUsuario(selector, usuarios, placeholder, allowEmpty = false) {
  const select = $(selector);
  if (!select) return;

  select.innerHTML = `<option value="">${placeholder}</option>`;
  usuarios.forEach((usuario) => {
    const option = document.createElement('option');
    option.value = usuario.id;
    option.textContent = `${usuario.nome} - ${usuario.setor}`;
    select.appendChild(option);
  });
}

async function carregarChamados() {
  try {
    const params = new URLSearchParams({
      q: $('#filtroTexto')?.value || '',
      status: $('#filtroStatus')?.value || '',
      area: $('#filtroArea')?.value || '',
      prioridade: $('#filtroPrioridade')?.value || '',
    });

    const { data, kpis } = await fetchJson(`${API_BASE}/chamados.php?${params.toString()}`);

    $('#kpiAbertos').textContent = kpis.abertos;
    $('#kpiAtendimento').textContent = kpis.atendimento;
    $('#kpiFinalizados').textContent = kpis.finalizados;

    const tbody = $('#listaChamados');
    const empty = $('#emptyState');
    tbody.innerHTML = '';

    if (!data.length) {
      empty.style.display = 'block';
      return;
    }

    empty.style.display = 'none';

    data.forEach((chamado) => {
      const tr = document.createElement('tr');
      tr.style.cursor = 'pointer';
      tr.addEventListener('click', () => {
        window.location.href = `chamado.php?id=${chamado.id}`;
      });

      tr.innerHTML = `
        <td>${chamado.id}</td>
        <td>${escapeHtml(chamado.titulo)}</td>
        <td>${escapeHtml(chamado.status)}</td>
        <td>${escapeHtml(chamado.prioridade)}</td>
        <td>${formatDate(chamado.aberto_em)}</td>
        <td>${escapeHtml(chamado.solicitante_nome)}</td>
        <td>${escapeHtml(chamado.responsavel_nome || 'Sem responsável')}</td>
        <td>${escapeHtml(chamado.area_solicitante)}</td>
        <td>${escapeHtml(chamado.area_responsavel)}</td>
      `;

      tbody.appendChild(tr);
    });
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function salvarChamado(event) {
  event.preventDefault();

  const solicitante = $('#solicitante');
  const responsavel = $('#responsavel');

  const payload = {
    titulo: $('#titulo').value,
    solicitante_id: solicitante.value || null,
    solicitante_nome: solicitante.selectedOptions[0]?.textContent?.split(' - ')[0] || 'Não informado',
    area_solicitante: $('#areaSolicitante').value,
    area_responsavel: $('#areaResponsavel').value,
    responsavel_id: responsavel.value || null,
    responsavel_nome: responsavel.value ? responsavel.selectedOptions[0]?.textContent?.split(' - ')[0] : null,
    categoria: $('#categoria').value,
    prioridade: $('#prioridade').value,
    impacto: $('#impacto').value,
    descricao: $('#descricao').value,
  };

  try {
    await fetchJson(`${API_BASE}/chamados.php`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });

    $('#formChamado').reset();
    closeModal('#modalChamado');
    showToast('Chamado salvo com sucesso.');
    carregarChamados();
  } catch (error) {
    showToast(error.message, 'error');
  }
}
