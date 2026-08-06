# API de Tabela Tarifária de Água

API REST para gerenciamento e cálculo de tarifas de água, com base em categorias de consumidor e faixas progressivas de consumo. Totalmente parametrizável via banco de dados (faixas e valores podem ser ajustados sem alteração de código).

## Stack Tecnológica

- **Linguagem**: Java 21
- **Framework**: Spring Boot 4.1.0
- **Banco de Dados**: PostgreSQL 16
- **Build**: Maven
- **ORM**: Spring Data JPA / Hibernate
- **Documentação**: springdoc-openapi (Swagger UI)

## Decisões de Arquitetura

- **Camadas Controller → Service → Repository**, isolando regra de negócio (cálculo progressivo, validação de faixas) da camada web.
- **Categorias como entidade própria** (não enum), atendendo ao requisito de parametrização total sem alteração de código.
- **Exclusão lógica** de tabelas tarifárias, preservando histórico.
- **`BigDecimal`** para todos os valores monetários, evitando erros de arredondamento binário.
- **Tabela tarifária vigente por data**: quando o cálculo não especifica qual tabela usar, o sistema seleciona a mais recente cuja `dataVigencia` já tenha passado (com desempate por `id` em caso de datas iguais).

## Pré-requisitos

- [JDK 21](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Docker e Docker Compose](https://docs.docker.com/compose/install/)

## Configuração do Banco de Dados

O projeto usa Docker Compose para subir o PostgreSQL localmente:

```bash
docker compose up -d
```

Isso cria um container Postgres com:
- **Banco**: `tarifa_agua`
- **Usuário**: `tarifa_user`
- **Senha**: `tarifa_pass`
- **Porta exposta**: `5434` (host) → `5432` (container)

> A porta `5434` foi escolhida para evitar conflitos com outras instâncias de Postgres na máquina. Ajuste `docker-compose.yml` e `application.properties` se precisar de outra porta.

O schema é criado automaticamente pelo Hibernate na primeira execução (`spring.jpa.hibernate.ddl-auto=update`). O script SQL completo, gerado a partir desse schema, está disponível em [`sql/schema.sql`](sql/schema.sql). Ele pode ser usado para criar a estrutura manualmente, se preferir não depender do Hibernate para isso.

### Dados de exemplo (seed) — opcional

Um script com dados de exemplo (4 categorias exigidas pelo enunciado + faixas de consumo já parametrizadas) está disponível em [`sql/data.sql`](sql/data.sql). Não é carregado automaticamente (execute manualmente após o schema já existir):

**Linux/Mac/WSL:**
```bash
docker exec -i tarifa-agua-postgres psql -U tarifa_user -d tarifa_agua < sql/data.sql
```

**Windows (PowerShell):**
```powershell
cmd /c "docker exec -i tarifa-agua-postgres psql -U tarifa_user -d tarifa_agua < sql\data.sql"
```

## Instalação e Execução

```bash
git clone https://github.com/pegorara/tarifa-agua-api.git
cd tarifa-agua-api
docker compose up -d
mvn spring-boot:run
```

## Documentação interativa (Swagger)

Com a aplicação rodando, a documentação da API fica disponível em:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI (JSON)**: http://localhost:8080/v3/api-docs

## Endpoints

### Criar tabela tarifária

`POST /api/tabelas-tarifarias`

**Request:**
```json
{
  "nome": "Tabela 2026",
  "dataVigencia": "2026-01-01",
  "categorias": [
    {
      "categoria": "INDUSTRIAL",
      "faixas": [
        { "inicio": 0, "fim": 10, "valorUnitario": 1.00 },
        { "inicio": 11, "fim": 20, "valorUnitario": 2.00 }
      ]
    }
  ]
}
```

**Response** (`201 Created`):
```json
{
  "id": 1,
  "nome": "Tabela 2026",
  "dataVigencia": "2026-01-01",
  "categorias": [
    {
      "categoria": "INDUSTRIAL",
      "faixas": [
        { "inicio": 0, "fim": 10, "valorUnitario": 1.00 },
        { "inicio": 11, "fim": 20, "valorUnitario": 2.00 }
      ]
    }
  ]
}
```

Categorias informadas que ainda não existem no banco são criadas automaticamente.

### Listar tabelas tarifárias

`GET /api/tabelas-tarifarias`

**Response** (`200 OK`): array de objetos no mesmo formato da criação.

### Excluir tabela tarifária

`DELETE /api/tabelas-tarifarias/{id}`

**Response**: `204 No Content`

> Exclusão lógica: a tabela deixa de aparecer na listagem e de ser usada em cálculos futuros, mas permanece no banco para fins de histórico/auditoria.

### Calcular valor a pagar

`POST /api/calculos`

**Request:**
```json
{
  "categoria": "INDUSTRIAL",
  "consumo": 18
}
```

**Response** (`200 OK`):
```json
{
  "categoria": "INDUSTRIAL",
  "consumoTotal": 18,
  "valorTotal": 26.00,
  "detalhamento": [
    { "faixa": { "inicio": 0, "fim": 10 }, "m3Cobrados": 10, "valorUnitario": 1.00, "subtotal": 10.00 },
    { "faixa": { "inicio": 11, "fim": 20 }, "m3Cobrados": 8, "valorUnitario": 2.00, "subtotal": 16.00 }
  ]
}
```

> O cálculo usa a tabela tarifária **vigente**: a mais recente cuja `dataVigencia` já tenha passado, entre as tabelas ativas. Em caso de empate na data de vigência, prevalece a tabela cadastrada mais recentemente.

## Como Testar

### Via Swagger UI

Acesse http://localhost:8080/swagger-ui.html e execute os endpoints diretamente pelo navegador (os schemas de request já vêm preenchidos com exemplos).

### Via curl (Linux/Mac)

```bash
curl -X POST http://localhost:8080/api/tabelas-tarifarias \
  -H "Content-Type: application/json" \
  -d '{"nome":"Tabela 2026","dataVigencia":"2026-01-01","categorias":[{"categoria":"INDUSTRIAL","faixas":[{"inicio":0,"fim":10,"valorUnitario":1.00},{"inicio":11,"fim":20,"valorUnitario":2.00}]}]}'

curl -X POST http://localhost:8080/api/calculos \
  -H "Content-Type: application/json" \
  -d '{"categoria":"INDUSTRIAL","consumo":18}'
```

### Via PowerShell (Windows)

```powershell
$body = @{
    nome = "Tabela 2026"
    dataVigencia = "2026-01-01"
    categorias = @(
        @{
            categoria = "INDUSTRIAL"
            faixas = @(
                @{ inicio = 0; fim = 10; valorUnitario = 1.00 },
                @{ inicio = 11; fim = 20; valorUnitario = 2.00 }
            )
        }
    )
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8080/api/tabelas-tarifarias" -Method POST -ContentType "application/json" -Body $body

$calculoBody = @{ categoria = "INDUSTRIAL"; consumo = 18 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/calculos" -Method POST -ContentType "application/json" -Body $calculoBody
```