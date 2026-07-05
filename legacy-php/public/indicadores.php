<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>SI | Indicadores</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="css/indicadores.css" />
</head>

<body>
  <div class="app-shell">
    <div id="sidebarContainer"></div>

    <main class="content">
      <header class="topbar">
        <div>
          <h1>Indicadores</h1>
        </div>
      </header>

      <section class="indicator-grid" id="cardsIndicadores"></section>

      <section class="panel">
        <div class="panel-header">
          <div>
            <h2>Chamados por área</h2>
            <p>Distribuição atual por área responsável.</p>
          </div>
        </div>

        <div class="area-list" id="indicadoresArea"></div>
      </section>
    </main>
  </div>

  <div class="toast" id="toast"></div>
  <script src="js/app.js"></script>
  <script src="js/indicadores.js"></script>
</body>
</html>
