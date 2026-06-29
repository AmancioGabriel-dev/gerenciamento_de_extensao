# Sistema de Gerenciamento de Extensão — Spring Boot (Etapa 3)

Migração do trabalho de POO (aplicação de console) para uma **API REST com Spring Boot**.
A lógica de negócio (entidades, regras, validações) foi **preservada**; o que mudou foi a
"casca": menus de console viraram endpoints HTTP e as listas em memória viraram banco de dados.

## Tecnologias
- Java 21 (compila mesmo com JDK mais nova instalada)
- Spring Boot 3.3 (Web, Data JPA, Validation)
- Banco H2 em memória (zero instalação; reseta a cada reinício)
- Maven

## Como rodar

### Opção A — IntelliJ IDEA (recomendado)
1. Abra o IntelliJ → **File > Open** → selecione a pasta do projeto (que contém o `pom.xml`).
2. A IDE detecta o Maven e baixa as dependências automaticamente (precisa de internet na 1ª vez).
   - Se ela não recarregar, clique com o botão direito no `pom.xml` → **Add as Maven Project** /
     **Reload project**. (Se o projeto tinha config antiga, pode apagar a pasta `.idea` e reabrir.)
3. Rode a classe `GerenciamentoExtensaoApplication` (botão ▶ ao lado do `main`).

### Opção B — Linha de comando (precisa do Maven instalado)
```bash
mvn spring-boot:run
```

A aplicação sobe em **http://localhost:8080**.

## Recursos úteis
- **Console do banco H2**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:extensao` · Usuário: `sa` · Senha: (vazia)
- O SQL gerado pelo Hibernate aparece no console (ótimo para aprender).

## Usuários de exemplo (seed automático)
| Perfil       | Email            | Senha      |
|--------------|------------------|------------|
| Administrador| admin@univ.edu   | admin123   |
| Docente      | ana@univ.edu     | docente123 |
| Coordenador  | bruno@univ.edu   | coord123   |

## Estrutura do projeto
```
src/main/java/com/extensao/
├── model/        # enums (TipoUsuario, StatusOportunidade, ...)
├── entity/       # @Entity (Usuario, Oportunidade, ...) e @Embeddable (Permissao, HistoricoCargo)
├── repository/   # interfaces JpaRepository (acesso ao banco)
├── service/      # @Service — as REGRAS DE NEGÓCIO (coração do sistema)
├── dto/          # objetos de entrada/saída da API (records)
├── controller/   # @RestController — os endpoints HTTP
└── config/       # seed de dados e tratamento global de erros
```

## Mapa de conceitos: console → Spring Boot
| Antes (console)            | Agora (Spring Boot)            |
|----------------------------|--------------------------------|
| `Main` + menus + `Scanner` | `@RestController` (endpoints)   |
| `Services` com `List<>`    | `@Service` + `@Repository`      |
| `Entities`                 | `@Entity` / `@Embeddable`       |
| `new Service(dep)` no Main | injeção de dependência (`@Autowired` via construtor) |
| `System.out` de erro       | exceções → status HTTP (400/404/409) |

## Exemplos de requisições (HTTP)

> Use o Postman, Insomnia, ou `curl`. Corpo em JSON.

**Login**
```
POST /usuarios/login
{ "email": "ana@univ.edu", "senha": "docente123" }
```

**Listar todos os usuários (demonstra polimorfismo)**
```
GET /usuarios
```

**Autocadastro de discente**
```
POST /usuarios/discentes
{ "nome": "Joao", "email": "joao@univ.edu", "senha": "123", "matricula": 2024001, "semestre": 3 }
```

**Criar oportunidade** (use o id do docente; veja em `GET /usuarios/docentes`)
```
POST /oportunidades
{ "criadorId": 2, "titulo": "Projeto X", "descricao": "...", "modalidade": "PROJETO",
  "cargaHorariaPrevista": 40, "dataInicio": "2026-08-01", "dataFim": "2026-12-01", "vagas": 5 }
```

**Fluxo de aprovação da oportunidade**
```
PATCH /oportunidades/{id}/enviar-aprovacao   { "usuarioId": 2 }
PATCH /oportunidades/{id}/aprovar            { "usuarioId": 3 }   # coordenador
```

**Inscrever-se / aprovar inscrição**
```
POST  /inscricoes               { "discenteId": 4, "oportunidadeId": 1 }
PATCH /inscricoes/{id}/aprovar  { "usuarioId": 2 }   # docente responsável
```

**Solicitação de aproveitamento + deferimento**
```
POST  /aproveitamentos             { "discenteId": 4, "descricao": "PET", "cargaHorariaPleiteada": 110,
                                     "dataInicio": "2025-01-01", "dataFim": "2025-12-01", "documentoComprobatorio": "doc.pdf" }
PATCH /aproveitamentos/{id}/deferir { "avaliadorId": 3, "parecer": "Aprovado" }
```

**Outros**
```
GET  /perfis            # perfis e permissões
GET  /logs              # auditoria de todas as ações
GET  /aproveitamentos/pendentes/coordenador
```

## Observações sobre a migração
- A `Inscricao` ganhou um `id` (no console ela não tinha — não funcionaria em banco).
- O `Map<Modulo, Set<Acao>>` de permissões virou um conjunto do value object `Permissao`.
- Geração de `id`: antes `static contador++`, agora `@GeneratedValue` (o banco gera).
- `open-in-view` (padrão do Spring) mantém a sessão aberta durante a requisição,
  permitindo carregar relacionamentos `LAZY` na montagem dos DTOs.
```
