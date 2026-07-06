# Migração PHP → Java

## Visão geral

O sistema de chamados originalmente utilizava PHP 8.2, PDO, Apache e MySQL 8. As páginas PHP eram essencialmente HTML estático e consumiam os endpoints `chamados.php`, `usuarios.php`, `configuracoes.php` e `health.php`.

A aplicação ativa foi migrada para Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security e MySQL. O frontend permanece em HTML, CSS e JavaScript, servido por `src/main/resources/static`, mas agora consome exclusivamente a API Java.

O código PHP foi preservado em `legacy-php/` somente como referência histórica e não participa do build nem da execução.

## Arquitetura atual

O backend segue o fluxo:

```text
Controller → Service → Repository → MySQL
```

- `controller`: endpoints REST, contratos HTTP e endpoint de autenticação.
- `service`: regras de negócio, validações e transações.
- `repository`: persistência com Spring Data JPA.
- `entity`: entidades Hibernate.
- `dto`: contratos de entrada e saída, sem exposição das entidades sensíveis.
- `security`: integração dos usuários do MySQL com o Spring Security.
- `config`: segurança, inicialização de usuários e OpenAPI.
- `exception`: tratamento global de erros.

## Banco de dados

As tabelas principais são:

- `usuarios`: nome, e-mail, setor, perfil, status, nome de usuário e hash da senha.
- `chamados`: dados do chamado e referências opcionais ao solicitante e responsável.
- `chamado_movimentacoes`: histórico transacional dos chamados.
- `configuracoes`: configurações gerais do sistema.

Os campos de autenticação adicionados a `usuarios` são:

```text
nome_usuario VARCHAR(50) UNIQUE
senha VARCHAR(100)
```

O arquivo `migrations/001_create_tables.sql` utiliza `utf8mb4` e executa `SET NAMES utf8mb4`, preservando corretamente valores como `João`, `Recepção`, `Média` e `Crítica`. A migration também contém correções idempotentes para registros antigos que tenham sido importados com dupla codificação.

O projeto mantém `spring.jpa.hibernate.ddl-auto=update`, permitindo que o Hibernate complemente bancos existentes. As colunas de credenciais permanecem inicialmente anuláveis para não impedir a inicialização com registros legados sem login.

## Regras de chamados migradas

- A criação de chamado gera uma movimentação inicial na mesma transação.
- IDs válidos de solicitante e responsável prevalecem sobre nomes enviados pelo cliente.
- Status disponíveis: Novo, Em atendimento, Aguardando retorno e Finalizado.
- Prioridades disponíveis: Baixa, Média, Alta e Crítica.
- Impactos disponíveis: Baixo, Médio, Alto e Geral.
- Ao finalizar, `finalizado_em` é preenchido; ao reabrir, é limpo.
- Atualizações registram movimentações.
- A exclusão do chamado remove suas movimentações por cascade.
- A listagem aceita filtros exatos e pesquisa textual.

## Autenticação e segurança

A versão PHP original não possuía autenticação real e simulava o usuário atual no `localStorage`. Durante a migração, essa limitação histórica foi eliminada: a versão Java implementa autenticação real integrada ao banco de dados.

A implementação atual utiliza:

- Spring Security;
- autenticação por nome de usuário e senha;
- usuários carregados do MySQL por `UsuarioDetailsService`;
- sessão HTTP;
- senhas armazenadas exclusivamente como hashes BCrypt;
- proteção CSRF com token enviado pelo helper `fetchJson`;
- logout real com invalidação da sessão.

Rotas públicas:

- `/login` e `/login.html`;
- arquivos necessários de CSS, JavaScript e assets;
- `/swagger-ui/**`;
- `/swagger-ui.html`;
- `/v3/api-docs/**`;
- `/api/auth/csrf`.

As páginas internas e os endpoints de negócio em `/api/**` exigem autenticação, ressalvados o endpoint público de CSRF e a documentação OpenAPI. O gerenciamento em `/usuarios.html` e `/api/usuarios/**` exige o perfil Administrador.

Os perfis são convertidos para as seguintes authorities:

| Perfil no sistema | Authority |
|---|---|
| Administrador | `ROLE_ADMINISTRADOR` |
| Atendente | `ROLE_ATENDENTE` |
| Solicitante | `ROLE_SOLICITANTE` |

Usuários com status `Inativo` não podem autenticar.

## Inicialização e migração de usuários

Usuários legados sem credenciais recebem uma única vez:

1. nome de usuário baseado no prefixo do e-mail;
2. sufixo numérico quando houver colisão;
3. senha inicial definida por `DEFAULT_USER_PASSWORD`;
4. hash BCrypt gerado antes da persistência.

Credenciais existentes não são recalculadas em cada inicialização.

O administrador configurado em `ADMIN_USERNAME` é criado quando esse nome de usuário ainda não existe, mesmo quando o banco já contém outros usuários. As variáveis utilizadas são:

```text
ADMIN_USERNAME
ADMIN_PASSWORD
ADMIN_EMAIL
DEFAULT_USER_PASSWORD
```

Os valores locais padrão estão documentados no README e devem ser alterados fora do ambiente de demonstração.

## API de usuários

O controller não retorna mais a entidade `Usuario`. Os contratos são:

- `UsuarioCreateRequest`: criação, com senha obrigatória.
- `UsuarioUpdateRequest`: atualização, com nova senha opcional.
- `UsuarioResponse`: resposta sem senha e sem hash.

Na atualização, senha nula, vazia ou composta somente por espaços mantém o hash atual. Uma nova senha válida gera outro hash BCrypt.

A tela `usuarios.html` permite:

- listar usuários;
- cadastrar usuários;
- editar nome, e-mail, nome de usuário, setor, perfil e status;
- definir uma nova senha opcional durante a edição.

A senha atual nunca é enviada ou preenchida no frontend.

## Usuário autenticado

O endpoint `GET /api/auth/me` retorna os dados seguros do usuário da sessão. A sidebar utiliza esse endpoint para apresentar:

- iniciais;
- nome;
- e-mail;
- dados correspondentes ao usuário realmente autenticado.

Não existem mais nome, e-mail ou setor fixos usados como identidade do usuário atual.

## Correspondência de endpoints

| PHP antigo | Endpoint Java | Situação |
|---|---|---|
| `api/chamados.php` GET | `GET /api/chamados` | Migrado e protegido |
| `api/chamados.php?id={id}` GET | `GET /api/chamados/{id}` | Migrado e protegido |
| `api/chamados.php` POST | `POST /api/chamados` | Migrado, protegido e com CSRF |
| `api/chamados.php?id={id}` PUT | `PUT /api/chamados/{id}` | Migrado, protegido e com CSRF |
| inexistente | `DELETE /api/chamados/{id}` | Adicionado |
| `api/usuarios.php` GET/POST | `GET/POST /api/usuarios` | Migrado e restrito a Administrador |
| inexistente | `GET/PUT/DELETE /api/usuarios/{id}` | Adicionado e restrito a Administrador |
| inexistente | `GET /api/auth/me` | Adicionado |
| inexistente | `GET /api/auth/csrf` | Adicionado |
| `api/configuracoes.php` GET/POST | `GET/PUT /api/configuracoes` | Migrado e protegido |
| `api/health.php` | healthcheck dos contêineres | Substituído |

## Frontend

As páginas foram convertidas de `.php` para `.html`, e as chamadas utilizam endpoints sem extensão PHP.

Foram preservadas e atualizadas as funcionalidades de:

- dashboard e listagem de chamados;
- filtros e indicadores;
- abertura, atualização, histórico e exclusão;
- chamados atribuídos e chamados do solicitante autenticado;
- relatórios;
- configurações;
- cadastro e edição de usuários;
- sidebar responsiva;
- tema visual escuro com identidade vermelha;
- login e logout.

## Testes e build

Os testes atuais utilizam JUnit 5 e Mockito. A dependência Spring Security Test está disponível no projeto, mas a suíte atual verifica o `UsuarioDetailsService` diretamente, sem MockMvc. Há cobertura para regras de chamados, codificação de senha, duplicidade de nome de usuário, preservação e alteração de senha e bloqueio de usuário inativo.

Resultados registrados durante a implementação:

```text
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

O build pode ser executado com:

```bash
mvn test
mvn clean package
```

Ou integralmente com Docker:

```bash
docker compose up -d --build
```

## Decisões e limitações

- O upload de anexos continua sem persistência real de arquivo.
- O `PUT` de chamado permanece integral; o frontend envia também os campos não alterados.
- Os fontes PHP são apenas históricos.
- A autenticação utiliza sessão HTTP, adequada ao sistema web interno; JWT não foi adotado.
- O usuário autenticado vem exclusivamente da sessão e de `GET /api/auth/me`. O `localStorage` permanece apenas para a preferência visual de sidebar recolhida, não para identidade ou autorização.
- A autorização por perfil é propositalmente simples: o gerenciamento de usuários é exclusivo do Administrador. Atendente e Solicitante recebem suas respectivas roles, mas não existe uma matriz granular adicional de permissões entre as demais funcionalidades.
- Swagger/OpenAPI permanece público para avaliação acadêmica; os endpoints de negócio documentados continuam sujeitos às regras de autenticação e autorização.
