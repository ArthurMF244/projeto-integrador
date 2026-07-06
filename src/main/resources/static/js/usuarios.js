let usuarioEmEdicaoId = null;
let usuariosCarregados = [];

document.addEventListener('DOMContentLoaded', () => {
  carregarUsuarios();
  bindUsuarios();
});

function bindUsuarios() {
  $('#btnNovoUsuario')?.addEventListener('click', abrirNovoUsuario);
  $('#btnFecharModalUsuario')?.addEventListener('click', fecharModalUsuario);
  $('#btnCancelarUsuario')?.addEventListener('click', fecharModalUsuario);
  $('#formUsuario')?.addEventListener('submit', salvarUsuario);
  $('#listaUsuarios')?.addEventListener('click', (event) => {
    const button = event.target.closest('[data-editar-usuario]');
    if (button) abrirEdicaoUsuario(Number(button.dataset.editarUsuario));
  });
  $('#btnToggleUsuarioSenha')?.addEventListener('click', (event) => {
    const input = $('#usuarioSenha');
    input.type = input.type === 'password' ? 'text' : 'password';
    event.currentTarget.innerHTML = `<i class="fa-solid fa-eye${input.type === 'password' ? '' : '-slash'}"></i>`;
  });
}

function abrirNovoUsuario() {
  usuarioEmEdicaoId = null;
  $('#formUsuario').reset();
  $('#usuarioSenha').required = true;
  $('#usuarioSenha').type = 'password';
  $('#usuarioSenhaLabel').textContent = 'Senha';
  $('#modalUsuarioContexto').textContent = 'Cadastro';
  $('#modalUsuarioTitulo').textContent = 'Novo usuário';
  $('#textoSalvarUsuario').textContent = 'Salvar usuário';
  openModal('#modalUsuario');
  $('#usuarioNome').focus();
}

function abrirEdicaoUsuario(id) {
  const usuario = usuariosCarregados.find((item) => item.id === id);
  if (!usuario) return;

  usuarioEmEdicaoId = id;
  $('#usuarioNome').value = usuario.nome;
  $('#usuarioEmail').value = usuario.email;
  $('#usuarioNomeUsuario').value = usuario.nomeUsuario;
  $('#usuarioSetor').value = usuario.setor;
  $('#usuarioPerfil').value = usuario.perfil;
  $('#usuarioStatus').value = usuario.status;
  $('#usuarioSenha').value = '';
  $('#usuarioSenha').required = false;
  $('#usuarioSenha').type = 'password';
  $('#usuarioSenhaLabel').textContent = 'Nova senha (opcional)';
  $('#modalUsuarioContexto').textContent = 'Edição';
  $('#modalUsuarioTitulo').textContent = 'Editar usuário';
  $('#textoSalvarUsuario').textContent = 'Salvar alterações';
  openModal('#modalUsuario');
  $('#usuarioNome').focus();
}

function fecharModalUsuario() {
  closeModal('#modalUsuario');
  usuarioEmEdicaoId = null;
  $('#formUsuario').reset();
}

async function carregarUsuarios() {
  try {
    const { data } = await fetchJson(`${API_BASE}/usuarios`);
    usuariosCarregados = data;
    const tbody = $('#listaUsuarios');
    tbody.innerHTML = '';

    data.forEach((usuario) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(usuario.nome)}</td>
        <td>${escapeHtml(usuario.nomeUsuario)}</td>
        <td>${escapeHtml(usuario.email)}</td>
        <td>${escapeHtml(usuario.setor)}</td>
        <td>${escapeHtml(usuario.perfil)}</td>
        <td><span class="user-status ${usuario.status === 'Inativo' ? 'inativo' : ''}">${escapeHtml(usuario.status)}</span></td>
        <td class="actions-column">
          <button class="icon-btn edit-user" type="button" data-editar-usuario="${usuario.id}" aria-label="Editar ${escapeHtml(usuario.nome)}" title="Editar usuário">
            <i class="fa-solid fa-pen"></i>
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function salvarUsuario(event) {
  event.preventDefault();
  const editando = usuarioEmEdicaoId !== null;
  const payload = {
    nome: $('#usuarioNome').value,
    email: $('#usuarioEmail').value,
    nomeUsuario: $('#usuarioNomeUsuario').value,
    senha: $('#usuarioSenha').value,
    setor: $('#usuarioSetor').value,
    perfil: $('#usuarioPerfil').value,
    status: $('#usuarioStatus').value,
  };

  try {
    await fetchJson(editando ? `${API_BASE}/usuarios/${usuarioEmEdicaoId}` : `${API_BASE}/usuarios`, {
      method: editando ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    });
    fecharModalUsuario();
    showToast(editando ? 'Usuário atualizado com sucesso.' : 'Usuário cadastrado com sucesso.');
    await carregarUsuarios();
  } catch (error) {
    showToast(error.message, 'error');
  }
}
