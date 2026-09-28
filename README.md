# BrasilTravel Aéreo

Sistema web de agência de viagens aéreas nacionais desenvolvido em Java Spring Boot, Thymeleaf e PostgreSQL.

## Execução local

1. Criar o banco PostgreSQL local:

```sql
CREATE DATABASE brasiltravel_aereo;
```

2. Ajustar `src/main/resources/application.properties` com usuário e senha do PostgreSQL.

3. Rodar o projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Acessar:

```text
http://localhost:8080
```

## Usuários de acesso

Administrador:

```text
admin@brasiltravel.com
Admin123
```

Cliente:

```text
cliente@brasiltravel.com
Cliente123
```

## Observações de dados

Por padrão, a aplicação recria dados comerciais de demonstração ao iniciar:

- destinos brasileiros;
- aeroportos nacionais;
- companhias aéreas;
- voos futuros de 27/09/2026 a 27/10/2026;
- solicitações históricas distribuídas de forma proporcional de 01/01/2026 a 27/09/2026;
- capacidades comerciais reduzidas nos voos para tornar o relatório de ocupação mais útil na demonstração.

Os relatórios foram pensados para usar o período histórico de 01/01/2026 a 27/09/2026.

## Relatórios disponíveis

- Viagens por companhia aérea;
- Ocupação e disponibilidade de voos;
- Demanda por destino.

Todos os relatórios possuem filtro obrigatório por status do pedido, com valor padrão `Finalizada`, além de período, companhia aérea e múltiplos aeroportos de origem/destino.


## Backup/dump do PostgreSQL

Para gerar um dump do banco local pelo PowerShell:

```powershell
pg_dump -U postgres -d brasiltravel_aereo -F p -f brasiltravel_aereo_dump.sql
```

Para restaurar em outro ambiente:

```powershell
psql -U postgres -d brasiltravel_aereo -f brasiltravel_aereo_dump.sql
```

## Ajuste de desempenho - Gerenciamento de voos

A listagem administrativa de voos exibe apenas voos futuros e usa paginação de 20 registros por página, preservando os filtros de aeroporto de origem e destino. Isso evita travamentos visuais quando o banco está povoado com muitas combinações de voos.
