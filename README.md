# Projeto Integrador - Sistema de Chamados

Aplicação acadêmica para abertura, atribuição, acompanhamento e encerramento de chamados. O backend foi migrado de PHP/PDO para Java 21 e Spring Boot 3; o frontend original foi preservado como HTML, CSS e JavaScript e agora consome somente a API REST Java.

A interface utiliza uma identidade visual própria inspirada em aplicações Java modernas: vermelho rubi como destaque, superfícies neutras, sidebar escura e modos claro/escuro. Nenhuma marca ou logotipo oficial do Java/Oracle é utilizado.

## Tecnologias

- Java 21, Spring Boot 3, Spring Web
- Spring Data JPA, Hibernate e MySQL 8
- Spring Security (sessão HTTP e BCrypt)
- Maven
- Bean Validation
- SpringDoc OpenAPI / Swagger UI
- JUnit 5 e Mockito
- HTML, CSS e JavaScript

## Arquitetura

O fluxo é `Controller → Service → Repository → MySQL`. Controllers tratam HTTP e validação; Services concentram regras e transações; Repositories usam Spring Data JPA; DTOs separam os contratos REST das entidades persistidas. O código fica sob `src/main/java/br/com/projeto/integrador`.

Entidades persistidas: `Chamado`, `Movimentacao`, `Usuario` e `Configuracao`. Um chamado pode referenciar um solicitante e um responsável e possui várias movimentações. Áreas, categorias, status e prioridades continuam como valores controlados, refletindo o banco legado.

Pacotes principais:

```text
controller/   Endpoints Spring MVC
service/      Regras de negócio e transações
repository/   Persistência Spring Data JPA
entity/       Mapeamento das tabelas MySQL
dto/          Contratos de entrada e saída
exception/    Exceções e tratamento global
config/       Configuração OpenAPI
```

## Configuração e execução

Requisitos locais: JDK 21, Maven 3.9+ e MySQL 8. Crie o banco e aplique `migrations/001_create_tables.sql`, ou suba o MySQL pronto com Docker:

```bash
docker compose up -d mysql
```

Valores padrão: banco `si_chamados`, usuário e senha `si_chamados`, porta local `3308`. Para outra instalação, defina:

```bash
DB_URL=jdbc:mysql://localhost:3306/si_chamados
DB_USERNAME=root
DB_PASSWORD=sua_senha
```

Há um modelo em `src/main/resources/application-example.properties`. Não versione credenciais reais.

```bash
mvn clean package
mvn spring-boot:run
```

Também é possível subir aplicação e banco em contêineres:

```bash
docker compose up -d --build
```

- Aplicação: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Páginas disponíveis: chamados/dashboard (`/`), detalhe do chamado, atribuídos a mim, meus chamados, usuários, indicadores, relatórios e configurações.

## Autenticação

O acesso ocorre em http://localhost:8080/login.html por **nome de usuário e senha**. O Spring Security autentica os usuários do MySQL, mantém a autenticação em sessão HTTP e armazena somente hashes BCrypt. A senha ou seu hash nunca fazem parte das respostas da API.

Perfis são mapeados para `ROLE_ADMINISTRADOR`, `ROLE_ATENDENTE` e `ROLE_SOLICITANTE`. Administradores podem gerenciar usuários; os demais perfis recebem `403 Forbidden` na página e na API de usuários. Swagger permanece público, mas a execução de endpoints protegidos requer uma sessão autenticada.

Quando o nome definido em `ADMIN_USERNAME` ainda não existe, um administrador inicial é criado uma única vez, inclusive em bancos legados que já possuam outros usuários. Configure antes da primeira execução:

```bash
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
ADMIN_EMAIL=admin@localhost
DEFAULT_USER_PASSWORD=alterar123
```

Os valores acima são apenas para desenvolvimento local e devem ser alterados. Usuários legados sem credenciais recebem, uma única vez, um nome baseado no prefixo do e-mail (com sufixo numérico em colisões) e a senha definida por `DEFAULT_USER_PASSWORD`, imediatamente convertida em BCrypt. Reinicializações não modificam credenciais já existentes.

## API

| Método | Endpoint | Ação |
|---|---|---|
| POST | `/api/chamados` | Criar |
| GET | `/api/chamados` | Listar e filtrar |
| GET | `/api/chamados/{id}` | Consultar |
| PUT | `/api/chamados/{id}` | Atualizar e registrar movimentação |
| DELETE | `/api/chamados/{id}` | Excluir |
| GET/POST/PUT/DELETE | `/api/usuarios` e `/api/usuarios/{id}` | CRUD de usuários |
| GET/PUT | `/api/configuracoes` | Consultar/salvar preferências |

Filtros de chamados: `status`, `prioridade`, `area`, `responsavel`, `solicitante` e `q`.

Exemplo para `POST /api/chamados`:

```json
{
  "titulo": "Erro no sistema",
  "solicitante_id": 1,
  "area_solicitante": "Financeiro",
  "area_responsavel": "TI - Sistemas",
  "responsavel_id": 3,
  "categoria": "Sistema",
  "prioridade": "Média",
  "impacto": "Baixo",
  "status": "Novo",
  "descricao": "Sistema apresenta erro ao acessar"
}
```

No Swagger, abra o endpoint, clique em **Try it out**, informe o JSON e execute. Respostas de validação e recursos inexistentes usam HTTP 400 e 404; criação retorna 201 e exclusão 204.

## Testes

```bash
mvn test
```

`ChamadoServiceTest` possui testes reais de criação com movimentação inicial, busca por ID, atualização sem duplicação e exclusão. Os testes são unitários e não exigem MySQL; a persistência da aplicação é MySQL.

## Migração

O relatório técnico completo está em [MIGRACAO_PHP_JAVA.md](MIGRACAO_PHP_JAVA.md). A implementação anterior foi preservada em `legacy-php/` apenas como referência e não participa da execução.
