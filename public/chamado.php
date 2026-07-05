<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>SI | Chamado</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="css/chamado.css" />
</head>

<body>
  <div class="app-shell">
    <div id="sidebarContainer"></div>

    <main class="content">
      <a class="back-link" href="index.php">
        <i class="fa-solid fa-arrow-left"></i>
        Voltar
      </a>

      <header class="topbar">
        <div>
          <h1 id="tituloPaginaChamado">Chamado</h1>
        </div>

        <div class="topbar-actions">
          <button class="btn primary" id="btnAbrirEdicao" type="button">
            <i class="fa-solid fa-pen-to-square"></i>
            Editar
          </button>
        </div>
      </header>

      <section class="ticket-layout">
        <article class="panel ticket-details-panel">
          <div class="panel-header clean-header">
            <div>
              <h2>Detalhes</h2>
              <p>Informações principais do chamado.</p>
            </div>
          </div>

          <div class="ticket-details-body">
            <div class="ticket-details-row">
              <div class="detail-item">
                <span>Nº</span>
                <strong id="resumoNumero"></strong>
              </div>

              <div class="detail-item">
                <span>Título</span>
                <strong id="detalheTituloChamado"></strong>
              </div>

              <div class="detail-item">
                <span>Status</span>
                <strong id="resumoStatus"></strong>
              </div>
            </div>

            <div class="ticket-details-row">
              <div class="detail-item">
                <span>Aberto em</span>
                <strong id="resumoAbertura"></strong>
              </div>

              <div class="detail-item">
                <span>Finalizado em</span>
                <strong id="detalheFinalizadoEm">-</strong>
              </div>

              <div class="detail-item">
                <span>Prioridade</span>
                <strong id="resumoPrioridade"></strong>
              </div>
            </div>

            <div class="ticket-details-row">
              <div class="detail-item">
                <span>Área solicitante</span>
                <strong id="detalheAreaSolicitante"></strong>
              </div>

              <div class="detail-item">
                <span>Área responsável</span>
                <strong id="detalheAreaResponsavel"></strong>
              </div>

              <div class="detail-item">
                <span>Impacto</span>
                <strong id="detalheImpacto"></strong>
              </div>
            </div>

            <div class="ticket-details-row">
              <div class="detail-item">
                <span>Solicitante</span>
                <strong id="detalheSolicitante"></strong>
              </div>

              <div class="detail-item">
                <span>Responsável</span>
                <strong id="detalheResponsavel"></strong>
              </div>

              <div class="detail-item">
                <span>Categoria</span>
                <strong id="detalheCategoria"></strong>
              </div>
            </div>

            <div class="ticket-description-box">
              <span>Descrição</span>
              <p id="detalheDescricao"></p>
            </div>
          </div>
        </article>

        <section class="panel movements-panel">
          <div class="panel-header clean-header">
            <div>
              <h2>Movimentações</h2>
            </div>
          </div>

          <div class="movement-list" id="historicoChamado"></div>
        </section>
      </section>
    </main>
  </div>

  <div class="modal-backdrop" id="modalEdicaoChamado" aria-hidden="true">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalEdicaoTitulo">
      <div class="modal-header">
        <div>
          <span class="eyebrow">Movimentação</span>
          <h2 id="modalEdicaoTitulo">Editar chamado</h2>
        </div>

        <button class="icon-btn" id="btnFecharModalEdicao" type="button" aria-label="Fechar edição">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>

      <form id="formEdicaoChamado" class="edit-ticket-form">
        <label>
          Status
          <select id="edicaoStatus" required>
            <option value="Novo">Novo</option>
            <option value="Em atendimento">Em atendimento</option>
            <option value="Aguardando retorno">Aguardando retorno</option>
            <option value="Finalizado">Finalizado</option>
          </select>
        </label>

        <label>
          Responsável
          <select id="edicaoResponsavel">
            <option value="">Sem responsável</option>
          </select>
        </label>

        <label>
          Prioridade
          <select id="edicaoPrioridade" required>
            <option value="Baixa">Baixa</option>
            <option value="Média">Média</option>
            <option value="Alta">Alta</option>
            <option value="Crítica">Crítica</option>
          </select>
        </label>

        <label>
          Área responsável
          <select id="edicaoAreaResponsavel" required>
            <option value="TI - Sistemas">TI - Sistemas</option>
            <option value="TI - Infraestrutura">TI - Infraestrutura</option>
            <option value="Financeiro">Financeiro</option>
            <option value="Recursos Humanos">Recursos Humanos</option>
            <option value="Compras">Compras</option>
          </select>
        </label>

        <label class="full">
          Descrição da movimentação
          <textarea id="edicaoDescricao" rows="5"></textarea>
        </label>

        <div class="upload-box full">
          <div>
            <strong>
              <i class="fa-solid fa-paperclip"></i>
              Anexo
            </strong>

            <p>Arquivo relacionado a esta movimentação.</p>
          </div>

          <input id="edicaoAnexo" type="file" />
        </div>

        <div class="modal-footer full">
          <button class="btn ghost" id="btnCancelarEdicao" type="button">
            <i class="fa-solid fa-ban"></i>
            Cancelar
          </button>

          <button class="btn primary" type="submit">
            <i class="fa-solid fa-floppy-disk"></i>
            Salvar
          </button>
        </div>
      </form>
    </div>
  </div>

  <div class="toast" id="toast"></div>
  <script src="js/app.js"></script>
  <script src="js/chamado.js"></script>
</body>
</html>
