<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>SI | Chamados</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="css/index.css" />
</head>

<body>
  <div class="app-shell">
    <div id="sidebarContainer"></div>

    <main class="content">
      <header class="topbar">
        <div>
          <h1>Chamados área</h1>
        </div>

        <div class="topbar-actions">
          <button class="btn primary" id="btnNovoChamado" type="button">
            <i class="fa-solid fa-plus"></i>
            Novo chamado
          </button>
        </div>
      </header>

      <section class="metrics" aria-label="Indicadores de chamados">
        <article class="metric-card">
          <div class="metric-top">
            <span class="metric-label">Abertos</span>
            <i class="fa-solid fa-folder-open"></i>
          </div>

          <strong id="kpiAbertos">0</strong>
        </article>

        <article class="metric-card">
          <div class="metric-top">
            <span class="metric-label">Em andamento</span>
            <i class="fa-solid fa-headset"></i>
          </div>

          <strong id="kpiAtendimento">0</strong>
        </article>

        <article class="metric-card">
          <div class="metric-top">
            <span class="metric-label">Finalizados</span>
            <i class="fa-solid fa-circle-check"></i>
          </div>

          <strong id="kpiFinalizados">0</strong>
        </article>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div>
            <h2>Listagem de chamados</h2>
          </div>

          <button class="btn secondary" id="btnFiltros" type="button">
            <i class="fa-solid fa-filter"></i>
            Filtros
          </button>
        </div>

        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Nº</th>
                <th>Chamado</th>
                <th>Status</th>
                <th>Prioridade</th>
                <th>Data abertura</th>
                <th>Solicitante</th>
                <th>Responsáveis</th>
                <th>Área sol.</th>
                <th>Área resp.</th>
              </tr>
            </thead>

            <tbody id="listaChamados"></tbody>
          </table>
        </div>

        <div class="empty-state" id="emptyState">
          <i class="fa-solid fa-magnifying-glass"></i>
          <strong>Nenhum chamado encontrado</strong>
          <p>Tente mudar os filtros ou cadastre um novo chamado.</p>
        </div>
      </section>
    </main>
  </div>

  <div class="modal-backdrop" id="modalFiltros" aria-hidden="true">
    <div class="modal modal-filtros" role="dialog" aria-modal="true" aria-labelledby="modalFiltrosTitulo">
      <div class="modal-header">
        <div>
          <span class="eyebrow">Pesquisa</span>
          <h2 id="modalFiltrosTitulo">Filtros de chamados</h2>
        </div>

        <button class="icon-btn" id="btnFecharModalFiltros" type="button" aria-label="Fechar filtros">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>

      <div class="filters-modal-body">
        <label>
          Buscar
          <input id="filtroTexto" type="search" />
        </label>

        <label>
          Status
          <select id="filtroStatus">
            <option value="">Todos</option>
            <option value="Novo">Novo</option>
            <option value="Em atendimento">Em atendimento</option>
            <option value="Aguardando retorno">Aguardando retorno</option>
            <option value="Finalizado">Finalizado</option>
          </select>
        </label>

        <label>
          Área responsável
          <select id="filtroArea">
            <option value="">Todas</option>
            <option value="TI - Sistemas">TI - Sistemas</option>
            <option value="TI - Infraestrutura">TI - Infraestrutura</option>
            <option value="Financeiro">Financeiro</option>
            <option value="Recursos Humanos">Recursos Humanos</option>
            <option value="Compras">Compras</option>
          </select>
        </label>

        <label>
          Prioridade
          <select id="filtroPrioridade">
            <option value="">Todas</option>
            <option value="Baixa">Baixa</option>
            <option value="Média">Média</option>
            <option value="Alta">Alta</option>
            <option value="Crítica">Crítica</option>
          </select>
        </label>
      </div>

      <div class="modal-footer filter-footer">
        <button class="btn ghost" id="btnLimparFiltros" type="button">
          <i class="fa-solid fa-eraser"></i>
          Limpar filtros
        </button>

        <button class="btn primary" id="btnBuscarFiltros" type="button">
          <i class="fa-solid fa-magnifying-glass"></i>
          Buscar
        </button>
      </div>
    </div>
  </div>

  <div class="modal-backdrop" id="modalChamado" aria-hidden="true">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalTitulo">
      <div class="modal-header">
        <div>
          <span class="eyebrow">Cadastro</span>
          <h2 id="modalTitulo">Novo chamado</h2>
        </div>

        <button class="icon-btn" id="btnFecharModal" type="button" aria-label="Fechar">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>

      <form id="formChamado" class="form-grid">
        <label class="full">
          Título do chamado *
          <input id="titulo" type="text" required />
        </label>

        <label>
          Solicitante *
          <select id="solicitante" required>
            <option value="">Selecione</option>
          </select>
        </label>

        <label>
          Área solicitante *
          <select id="areaSolicitante" required>
            <option value="">Selecione</option>
            <option>Recepção</option>
            <option>Faturamento</option>
            <option>Financeiro</option>
            <option>Recursos Humanos</option>
            <option>Compras</option>
            <option>Diretoria</option>
          </select>
        </label>

        <label>
          Área responsável *
          <select id="areaResponsavel" required>
            <option value="">Selecione</option>
            <option>TI - Sistemas</option>
            <option>TI - Infraestrutura</option>
            <option>Financeiro</option>
            <option>Recursos Humanos</option>
            <option>Compras</option>
          </select>
        </label>

        <label>
          Responsável
          <select id="responsavel">
            <option value="">Sem responsável</option>
          </select>
        </label>

        <label>
          Categoria *
          <select id="categoria" required>
            <option value="">Selecione</option>
            <option>Acesso</option>
            <option>Sistema</option>
            <option>Equipamento</option>
            <option>Rede / Internet</option>
            <option>Solicitação administrativa</option>
          </select>
        </label>

        <label>
          Prioridade *
          <select id="prioridade" required>
            <option value="">Selecione</option>
            <option>Baixa</option>
            <option>Média</option>
            <option>Alta</option>
            <option>Crítica</option>
          </select>
        </label>

        <label>
          Impacto
          <select id="impacto">
            <option>Baixo</option>
            <option>Médio</option>
            <option>Alto</option>
            <option>Geral</option>
          </select>
        </label>

        <label class="full">
          Descrição *
          <textarea id="descricao" rows="5" required></textarea>
        </label>

        <div class="upload-box full">
          <div>
            <strong>
              <i class="fa-solid fa-paperclip"></i>
              Anexo visual
            </strong>

            <p>Campo demonstrativo para evidenciar que o sistema poderá receber prints ou documentos na próxima etapa.</p>
          </div>

          <input id="anexo" type="file" />
        </div>

        <div class="modal-footer full">
          <button class="btn ghost" id="btnCancelar" type="button">
            <i class="fa-solid fa-ban"></i>
            Cancelar
          </button>

          <button class="btn primary" type="submit">
            <i class="fa-solid fa-floppy-disk"></i>
            Salvar chamado
          </button>
        </div>
      </form>
    </div>
  </div>

  <div class="toast" id="toast"></div>
  <script src="js/app.js"></script>
  <script src="js/index.js"></script>
</body>
</html>
