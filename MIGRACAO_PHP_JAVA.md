# Migração PHP → Java

## Diagnóstico da aplicação anterior

O projeto era um sistema de chamados em PHP 8.2 puro, PDO, Apache, MySQL 8 e frontend HTML/CSS/JavaScript. As páginas `.php` não continham renderização dinâmica: eram HTML estático. O backend estava em quatro endpoints (`chamados.php`, `usuarios.php`, `configuracoes.php`, `health.php`) e helpers compartilhados.

Não foi encontrado login, senha, middleware de autorização ou sessão PHP. A interface simula o usuário atual por `localStorage`, com Arthur como fallback. Por isso não foi inventada uma camada complexa de segurança nesta entrega acadêmica.

### Banco analisado

- `usuarios`: PK `id`; nome, e-mail único, setor, perfil e status.
- `chamados`: PK `id`; FKs opcionais `solicitante_id` e `responsavel_id` para `usuarios`; dados desnormalizados dos nomes; título, áreas, categoria, prioridade, impacto, status, descrição e datas.
- `chamado_movimentacoes`: PK `id`; FK obrigatória para chamado com exclusão em cascata e FKs opcionais para usuários.
- `configuracoes`: registro singleton de id 1.

`Pessoa` e `Area` não existiam. Criá-las duplicaria `Usuario` e valores textuais já usados pela aplicação, portanto foram deliberadamente omitidas.

### Regras identificadas e migradas

- Criação de chamado gera uma movimentação inicial na mesma transação.
- IDs de solicitante/responsável válidos prevalecem sobre nomes enviados pelo cliente.
- Status: Novo, Em atendimento, Aguardando retorno e Finalizado.
- Prioridades: Baixa, Média, Alta e Crítica; impactos: Baixo, Médio, Alto e Geral.
- Ao finalizar, `finalizado_em` é preenchido; ao reabrir, é limpo.
- Toda atualização registra uma movimentação.
- Exclusão do chamado remove suas movimentações pelo relacionamento/cascade.
- Listagem aceita filtros exatos e pesquisa textual.

## Nova arquitetura

Aplicação Maven em Java 21/Spring Boot 3:

- `controller`: contratos REST e códigos HTTP.
- `service`: regras, validação de domínio e transações.
- `repository`: Spring Data JPA.
- `entity`: mapeamento Hibernate do esquema MySQL.
- `dto`: entrada e saída da API.
- `exception`: erros globais sem exposição de stack trace.
- `config`: metadados OpenAPI.

O frontend foi movido para `src/main/resources/static`, páginas foram convertidas para `.html`, links foram atualizados e todas as chamadas agora apontam para endpoints sem extensão PHP.

## Correspondência de endpoints

| PHP antigo | Endpoint Java | Status |
|---|---|---|
| `api/chamados.php` GET | `GET /api/chamados` | Migrado |
| `api/chamados.php?id={id}` GET | `GET /api/chamados/{id}` | Migrado |
| `api/chamados.php` POST | `POST /api/chamados` | Migrado |
| `api/chamados.php?id={id}` PUT | `PUT /api/chamados/{id}` | Migrado |
| inexistente | `DELETE /api/chamados/{id}` | Adicionado |
| `api/usuarios.php` GET/POST | `GET/POST /api/usuarios` | Migrado |
| inexistente | `GET/PUT/DELETE /api/usuarios/{id}` | Adicionado |
| `api/configuracoes.php` GET/POST | `GET/PUT /api/configuracoes` | Migrado |
| `api/health.php` | healthcheck da aplicação/contêiner | Substituído |

## Telas e funções migradas

Listagem, filtros, KPIs, abertura e detalhe de chamados; atualização e histórico; “atribuídos a mim”; “meus chamados”; indicadores; relatório por área; cadastro/listagem de usuários; configurações, tema e sidebar. CSS, Font Awesome e componentes visuais foram preservados.

## Decisões e limitações

- A API usa DTOs no CRUD principal e Bean Validation.
- O `PUT` de chamado é integral. O frontend envia os campos imutados junto com os campos editados.
- Upload era apenas demonstrativo no PHP e continua sem persistência de arquivo.
- A identidade local continua demonstrativa, pois não havia autenticação real para migrar. Spring Security/JWT ficou fora do núcleo acadêmico solicitado.
- Os fontes PHP permanecem em `legacy-php/`, desconectados do build e da execução.
- O esquema existente foi reaproveitado; `ddl-auto=update` permite ao Hibernate conferir/complementar o banco.
