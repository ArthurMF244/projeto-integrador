<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>SI | Configurações</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="css/configuracoes.css" />
</head>

<body>
  <div class="app-shell">
    <div id="sidebarContainer"></div>

    <main class="content">
      <header class="topbar">
        <div>
          <h1>Configurações</h1>
        </div>
      </header>

      <section class="panel">
        <div class="panel-header">
          <div>
            <h2>Preferências do sistema</h2>
            <p>Ajustes gerais da interface.</p>
          </div>
        </div>

        <form class="settings-form" id="formConfiguracoes">
          <label>
            Nome do sistema
            <input id="configNomeSistema" type="text" required />
          </label>

          <label>
            Tema padrão
            <select id="configTema">
              <option value="">Claro</option>
              <option value="dark">Escuro</option>
            </select>
          </label>

          <label>
            E-mail de suporte
            <input id="configEmailSuporte" type="email" required />
          </label>

          <div class="modal-footer full">
            <button class="btn primary" type="submit">
              <i class="fa-solid fa-floppy-disk"></i>
              Salvar configurações
            </button>
          </div>
        </form>
      </section>
    </main>
  </div>

  <div class="toast" id="toast"></div>
  <script src="js/app.js"></script>
  <script src="js/configuracoes.js"></script>
</body>
</html>
