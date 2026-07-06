document.addEventListener('DOMContentLoaded', () => {
  carregarConfiguracoes();
  $('#formConfiguracoes')?.addEventListener('submit', salvarConfiguracoes);
});

async function carregarConfiguracoes() {
  try {
    const { data } = await fetchJson(`${API_BASE}/configuracoes`);

    if (!data) return;

    $('#configNomeSistema').value = data.nome_sistema || '';
    $('#configTema').value = data.tema || '';
    $('#configEmailSuporte').value = data.email_suporte || '';
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function salvarConfiguracoes(event) {
  event.preventDefault();

  const payload = {
    nome_sistema: $('#configNomeSistema').value,
    tema: $('#configTema').value,
    email_suporte: $('#configEmailSuporte').value,
  };

  try {
    await fetchJson(`${API_BASE}/configuracoes`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });

    applyTheme(payload.tema);
    showToast('Configurações salvas com sucesso.');
  } catch (error) {
    showToast(error.message, 'error');
  }
}
