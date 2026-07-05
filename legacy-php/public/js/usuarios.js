document.addEventListener('DOMContentLoaded', () => {
  carregarUsuarios();
  bindUsuarios();
});

function bindUsuarios() {
  $('#btnNovoUsuario')?.addEventListener('click', () => openModal('#modalUsuario'));
  $('#btnFecharModalUsuario')?.addEventListener('click', () => closeModal('#modalUsuario'));
  $('#btnCancelarUsuario')?.addEventListener('click', () => closeModal('#modalUsuario'));
  $('#formUsuario')?.addEventListener('submit', salvarUsuario);
}

async function carregarUsuarios() {
  try {
    const { data } = await fetchJson(`${API_BASE}/usuarios.php`);
    const tbody = $('#listaUsuarios');
    tbody.innerHTML = '';

    data.forEach((usuario) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(usuario.nome)}</td>
        <td>${escapeHtml(usuario.email)}</td>
        <td>${escapeHtml(usuario.setor)}</td>
        <td>${escapeHtml(usuario.perfil)}</td>
        <td>${escapeHtml(usuario.status)}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function salvarUsuario(event) {
  event.preventDefault();

  const payload = {
    nome: $('#usuarioNome').value,
    email: $('#usuarioEmail').value,
    setor: $('#usuarioSetor').value,
    perfil: $('#usuarioPerfil').value,
    status: $('#usuarioStatus').value,
  };

  try {
    await fetchJson(`${API_BASE}/usuarios.php`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });

    $('#formUsuario').reset();
    closeModal('#modalUsuario');
    showToast('Usuário salvo com sucesso.');
    carregarUsuarios();
  } catch (error) {
    showToast(error.message, 'error');
  }
}
