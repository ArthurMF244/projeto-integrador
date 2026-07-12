let perfilAtual = null;
let cropper = null;
let fotoObjectUrl = null;

document.addEventListener('DOMContentLoaded', async () => {
  $('#btnAlterarFoto').addEventListener('click', () => $('#fotoInput').click());
  $('#fotoInput').addEventListener('change', selecionarFoto);
  $('#btnRemoverFoto').addEventListener('click', removerFoto);
  $('#formPerfil').addEventListener('submit', salvarPerfil);
  $('#formSenha').addEventListener('submit', alterarSenha);
  $$('input[name="tema"]').forEach((input) => input.addEventListener('change', () => applyTheme(input.value)));
  $('#btnZoomIn').addEventListener('click', () => cropper?.zoom(0.1));
  $('#btnZoomOut').addEventListener('click', () => cropper?.zoom(-0.1));
  $('#btnConfirmarCrop').addEventListener('click', confirmarRecorte);
  $('#btnCancelarCrop').addEventListener('click', fecharCrop);
  $('#btnCancelarCropTopo').addEventListener('click', fecharCrop);
  await carregarPerfil();
});

async function carregarPerfil() {
  try {
    preencherPerfil(await fetchJson(`${API_BASE}/perfil`));
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function preencherPerfil(perfil) {
  perfilAtual = perfil;
  $('#perfilNome').value = perfil.nome;
  $('#perfilEmail').value = perfil.email;
  $('#perfilNomeUsuario').value = perfil.nomeUsuario;
  $('#perfilSetor').value = perfil.setor;
  $('#perfilPerfil').value = perfil.perfil;
  $('#perfilStatus').value = perfil.status;
  const tema = perfil.tema || 'dark';
  const radio = `input[name="tema"][value="${tema}"]`;
  $(radio).checked = true;
  applyTheme(tema);
  atualizarAvatar(perfil);
}

function atualizarAvatar(perfil) {
  const avatar = $('#profileAvatar');
  if (perfil.fotoUrl) {
    avatar.innerHTML = `<img src="${escapeHtml(perfil.fotoUrl)}?v=${Date.now()}" alt="Foto de ${escapeHtml(perfil.nome)}">`;
    $('#btnRemoverFoto').hidden = false;
  } else {
    const partes = perfil.nome.trim().split(/\s+/);
    avatar.textContent = ((partes[0]?.[0] || '') + (partes.length > 1 ? partes.at(-1)[0] : '')).toUpperCase() || 'U';
    $('#btnRemoverFoto').hidden = true;
  }
}

async function salvarPerfil(event) {
  event.preventDefault();
  const tema = $('input[name="tema"]:checked')?.value;
  try {
    const atualizado = await fetchJson(`${API_BASE}/perfil`, {
      method: 'PUT',
      body: JSON.stringify({ nome: $('#perfilNome').value, email: $('#perfilEmail').value, tema }),
    });
    preencherPerfil(atualizado);
    await renderSidebar();
    showToast('Perfil atualizado com sucesso.');
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function alterarSenha(event) {
  event.preventDefault();
  const senhaAtual = $('#senhaAtual').value;
  const novaSenha = $('#novaSenha').value;
  const confirmacaoSenha = $('#confirmacaoSenha').value;
  if (!senhaAtual && !novaSenha && !confirmacaoSenha) return;
  if (!senhaAtual || !novaSenha || !confirmacaoSenha) {
    showToast('Preencha os três campos para alterar a senha.', 'error');
    return;
  }
  try {
    const resposta = await fetchJson(`${API_BASE}/perfil/senha`, {
      method: 'PUT',
      body: JSON.stringify({ senhaAtual, novaSenha, confirmacaoSenha }),
    });
    event.currentTarget.reset();
    showToast(resposta.mensagem);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function selecionarFoto(event) {
  const arquivo = event.target.files[0];
  event.target.value = '';
  if (!arquivo) return;
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(arquivo.type) || arquivo.size > 5 * 1024 * 1024) {
    showToast('Selecione uma imagem JPG, PNG ou WEBP de até 5 MB.', 'error');
    return;
  }
  fotoObjectUrl = URL.createObjectURL(arquivo);
  $('#cropImage').src = fotoObjectUrl;
  openModal('#modalCrop');
  cropper?.destroy();
  cropper = new Cropper($('#cropImage'), {
    aspectRatio: 1,
    viewMode: 1,
    dragMode: 'move',
    autoCropArea: 1,
    background: false,
    preview: '.crop-preview',
  });
}

function fecharCrop() {
  cropper?.destroy();
  cropper = null;
  if (fotoObjectUrl) URL.revokeObjectURL(fotoObjectUrl);
  fotoObjectUrl = null;
  closeModal('#modalCrop');
}

function confirmarRecorte() {
  const canvas = cropper?.getCroppedCanvas({ width: 512, height: 512, imageSmoothingQuality: 'high' });
  if (!canvas) return;
  canvas.toBlob(enviarFotoRecortada, 'image/webp', 0.9);
}

async function enviarFotoRecortada(blob) {
  if (!blob) {
    showToast('Não foi possível gerar a imagem recortada.', 'error');
    return;
  }
  const dados = new FormData();
  dados.append('foto', blob, 'perfil.webp');
  try {
    const atualizado = await fetchJson(`${API_BASE}/perfil/foto`, { method: 'POST', body: dados });
    fecharCrop();
    preencherPerfil(atualizado);
    await renderSidebar();
    showToast('Foto atualizada com sucesso.');
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function removerFoto() {
  try {
    const atualizado = await fetchJson(`${API_BASE}/perfil/foto`, { method: 'DELETE' });
    preencherPerfil(atualizado);
    await renderSidebar();
    showToast('Foto removida com sucesso.');
  } catch (error) {
    showToast(error.message, 'error');
  }
}
