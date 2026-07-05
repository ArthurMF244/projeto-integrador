<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>SI | Usuários</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="css/usuarios.css" />
</head>

<body>
  <div class="app-shell">
    <div id="sidebarContainer"></div>

    <main class="content">
      <header class="topbar">
        <div>
          <h1>Usuários</h1>
        </div>

        <div class="topbar-actions">
          <button class="btn primary" id="btnNovoUsuario" type="button">
            <i class="fa-solid fa-user-plus"></i>
            Novo usuário
          </button>
        </div>
      </header>

      <section class="panel">
        <div class="panel-header">
          <div>
            <h2>Gerenciamento de usuários</h2>
            <p>Cadastre e acompanhe usuários do sistema.</p>
          </div>
        </div>

        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Nome</th>
                <th>E-mail</th>
                <th>Setor</th>
                <th>Perfil</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody id="listaUsuarios"></tbody>
          </table>
        </div>
      </section>
    </main>
  </div>

  <div class="modal-backdrop" id="modalUsuario" aria-hidden="true">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalUsuarioTitulo">
      <div class="modal-header">
        <div>
          <span class="eyebrow">Cadastro</span>
          <h2 id="modalUsuarioTitulo">Novo usuário</h2>
        </div>

        <button class="icon-btn" id="btnFecharModalUsuario" type="button" aria-label="Fechar usuário">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>

      <form id="formUsuario" class="user-form">
        <label>
          Nome
          <input id="usuarioNome" type="text" required />
        </label>

        <label>
          E-mail
          <input id="usuarioEmail" type="email" required />
        </label>

        <label>
          Setor
          <input id="usuarioSetor" type="text" required />
        </label>

        <label>
          Perfil
          <select id="usuarioPerfil">
            <option>Administrador</option>
            <option>Atendente</option>
            <option>Solicitante</option>
          </select>
        </label>

        <label>
          Status
          <select id="usuarioStatus">
            <option>Ativo</option>
            <option>Inativo</option>
          </select>
        </label>

        <div class="modal-footer full">
          <button class="btn ghost" id="btnCancelarUsuario" type="button">
            <i class="fa-solid fa-ban"></i>
            Cancelar
          </button>

          <button class="btn primary" type="submit">
            <i class="fa-solid fa-floppy-disk"></i>
            Salvar usuário
          </button>
        </div>
      </form>
    </div>
  </div>

  <div class="toast" id="toast"></div>
  <script src="js/app.js"></script>
  <script src="js/usuarios.js"></script>
</body>
</html>
