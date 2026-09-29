# BrasilTravel

Sistema web de agência de viagens aéreas nacionais desenvolvido em Java Spring Boot, Thymeleaf e PostgreSQL.

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-view-005F0F?logo=thymeleaf&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-relacional-4169E1?logo=postgresql&logoColor=white)
![Render](https://img.shields.io/badge/Render-Deploy-46E3B7?logo=render&logoColor=white)

## Sobre o projeto

O **BrasilTravel** é uma aplicação de banco de dados relacional voltada à gestão de voos nacionais, desenvolvida como Fase 1 do projeto da disciplina de Banco de Dados II.

O sistema permite consultar destinos, aeroportos, companhias aéreas e voos disponíveis, além de registrar solicitações de viagem — associando cliente e voo, com acompanhamento de status (ex.: *Finalizada*) e geração de relatórios gerenciais.

A modelagem completa (esquema conceitual e dicionário de dados) está disponível no documento em [`/docs`](./docs).

## Funcionalidades

| Categoria | Operações |
| --- | --- |
| Destinos | Cadastro, consulta, atualização e remoção |
| Aeroportos | Cadastro, consulta, atualização e remoção |
| Companhias aéreas | Cadastro, consulta, atualização e remoção |
| Voos | Cadastro, consulta, atualização e remoção |
| Solicitações (associativa) | Registro de solicitação de viagem, acompanhamento e atualização de status |
| Relatórios | Viagens por companhia aérea, ocupação e disponibilidade de voos, demanda por destino |

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 17 |
| Framework | Spring Boot |
| Camada de visão | Thymeleaf |
| Banco de dados | PostgreSQL |
| Gerenciador de dependências | Maven |
| Hospedagem | Render |

## Execução local

1. Criar o banco PostgreSQL local:

```sql
CREATE DATABASE brasiltravel;
```

2. Ajustar `src/main/resources/application.properties` com usuário e senha do PostgreSQL.

3. Rodar o projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

> Em Linux/Mac, use `./mvnw spring-boot:run`.

4. Acessar:

```text
http://localhost:8080
```

## Usuários de acesso

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Administrador | `admin@brasiltravel.com` | `Admin123` |
| Cliente | `cliente@brasiltravel.com` | `Cliente123` |

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
pg_dump -U postgres -d brasiltravel -F p -f brasiltravel_dump.sql
```

Para restaurar em outro ambiente:

```powershell
psql -U postgres -d brasiltravel -f brasiltravel_dump.sql
```

## Ajuste de desempenho — Gerenciamento de voos

A listagem administrativa de voos exibe apenas voos futuros e usa paginação de 20 registros por página, preservando os filtros de aeroporto de origem e destino. Isso evita travamentos visuais quando o banco está povoado com muitas combinações de voos.

## Deploy (Render)

O deploy usa o `Dockerfile` do projeto (Web Service do tipo Docker) com um PostgreSQL do Render.

| Variável | Valor |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host-interno-render>:5432/<nome-do-banco>` |
| `SPRING_DATASOURCE_USERNAME` | fornecido pelo banco PostgreSQL no Render |
| `SPRING_DATASOURCE_PASSWORD` | fornecido pelo banco PostgreSQL no Render |
| `PORT` | definida automaticamente pelo Render |

Aplicação publicada: *(inserir o link do Render)*

## Vídeo de demonstração

[https://youtu.be/codigo-do-video](https://youtu.be/codigo-do-video) *(atualizar com o link real)*

## Equipe

| Integrante |
| --- |
| Isadora Pimenta de Oliveira |
| Luís Felipe dos Anjos de Carvalho |

**Professora:** Rebeca Schroeder Freitas
**Disciplina:** Banco de Dados II
**Curso/Turma:** TADS 2026/02
