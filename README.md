# Academia

> Em desenvolvimento

## Índice 
* [Sobre o projeto](#sobre-o-projeto)
* [Funcionalidades](#funcionalidades)
* [Tecnologias utilizadas](#tecnologias-utilizadas)
* [Organização dos Serviços](#organização-dos-serviços)
* [Comunicação entre os Serviços](#comunicação-entre-os-serviços)
* [Pré-requisitos](#pré-requisitos)
* [Configuração](#configuração)
* [Como executar](#como-executar)
* [Autenticação](#autenticação)
* [Demonstração](#demonstração)
* [Testes Automatizados](#testes-automatizados)
* [Comandos úteis](#comandos-úteis)
* [Limitações e próximas melhorias](#limitações-e-próximas-melhorias)

## Sobre o projeto

O projeto foi desenvolvido como um sistema, incluindo back-end e front-end, para a realização de cadastros de alunos e aulas em uma academia. Ele engloba três APIs de microserviços para realizar o cadastro das aulas, dos alunos, e a implementação das notificações com o kafka, com o front-end sendo desenvolvido com react, tendo node.js como ambiente de execução.

## Funcionalidades

### Alunos e planos
- Cadastro, consulta, atualização e exclusão de planos;
- Cadastro, consulta, atualização e exclusão de alunos;
- Bloqueio de alunos e planos duplicados;
- Verificação de validade de e-mail e cpf no cadastro;
- Associação de alunos e planos.

### Professores e aulas
- Cadastro, consulta, atualização e exclusão de aulas;
- Cadastro, consulta, atualização e exclusão de professores;
- Bloqueio de professores e aulas duplicadas;
- Verificação de validade de e-mail e cpf no cadastro;
- Associação de professores e aulas.

### Matriculas 
- Cadastro, consulta, exclusão e atualização de matriculas;
- Validação da existência do aluno por comunicação entre os serviços;
- Verificação da disponibilidade de vagas na aula ao fazer a matrícula;
- Bloqueio de matrículas duplicadas;
- Cancelamento e reativação de matrículas;
- Verificação de status do aluno e disponibilidade de vagas antes da reativação.

### Check-in
- Registro da presença do aluno em uma aula;
- Verificação do status da matrícula antes de validar check-in;
- Registro da data e horário do check-in;
- Exclusão do check-in.

### Notificações
- Publicação de eventos de matrícula criados no Apache Kafka;
- Consumo dos eventos pelo serviço de notificações;
- Simulação de notificações nos logs;
- Processamento de eventos pendentes após a reinicialização do consumidor.

### Segurança e Validação
- Autenticação HTTP Basic para operações de escrita nas APIs;
- Consultas GET públicas;
- Validação dos dados recebidos;
- Respostas padronizadas conformes os erros específicos das aplicações.

## Tecnologias utilizadas

- Java 17;
- Spring Boot;
- Spring Data JPA;
- Spring Security;
- PostgreSQL 17;
- Apache Kafka;
- Docker Compose;
- JUnit e Mockito;
- React;
- Vite;
- JavaScript;
- HTML e CSS;
- ESLint;
- Nginx.

## Organização dos Serviços

Serviço|Responsabilidade|Porta no computador|
---------------------------------------------
|alunos-service|Cadastro de alunos e planos|8081|
|aula-service|Cadastro de professores, aulas, check-ins e matriculas|8082|
|notificacoes-service|Simulação de notificações nos logs, e consumo de eventos de matrícula|Não possui endpoint HTTP|
|academia-frontend|Tela estilizada integrando os outros serviços|8080|

O alunos-service e o aula-service possuem bancos de dados PostgreSQL separados;

O notificacoes-service não possui banco de dados.

## Comunicação entre os Serviços

Cadastro da matrícula:
1. O aula-service consulta o alunos-service por HTTP para validar a existência do aluno.
2. Verifica se a aula existe dentro do próprio serviço.
3. Salva a matrícula no banco de dados.
4. Publica um evento no tópico 'academia.matriculas.criadas'.
5. O notificacoes-service consome o evento e registra uma notificação no log.

Exemplo de evento:
```JSON 
{
    "idEvento": "identificador do evento"
    "idAluno" : 1
    "idAula" : 5
    "dataInscricao" "2026-10-05"
}
```

## Pré-requisitos

### Para executar o projeto completo com Docker

- Docker Desktop instalado e iniciado.
- Docker Compose disponível.
- Arquivo `.env` configurado conforme o `.env.example`.
- Portas 8080, 8081 e 8082 disponíveis.
- Acesso à internet ao realizar a primeira construção para baixar as imagens e dependências.

Não é necessário instalar Java, Node.js, PostgreSQL ou Kafka no computador para executar pelo Docker.

### Para desenvolver o front-end localmente

- Node.js 24 LTS e npm.

### Para executar o back-end ou os testes Java localmente

- JDK 17 instalado.
- Variável `JAVA_HOME` configurada.

O projeto inclui o Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

## Configuração

Crie um arquivo `.env` na mesma pasta do `compose.yaml`

```.env

ALUNOS_DB_PASSWORD=defina_senha_local
AULAS_DB_PASSWORD=defina_outra_senha_local

ALUNOS_API_USERNAME=defina_nome_de_usuario
ALUNOS_API_PASSWORD=defina_senha_local

AULA_API_USERNAME=defina_nome_de_usuario
AULA_API_PASSWORD=defina_outra_senha_local
```

O `.env` não deve ser enviado ao git, logo deve estar incluido no `.gitignore` da pasta geral do projeto.

As credencias (senhas) dos bancos, definidas no `.env` não são as mesmas de autenticação das APIs.

## Como executar

### 1. Configure as variáveis de ambiente

Na pasta `Back-end`, copie `.env.example` para `.env` e preencha
as credenciais dos bancos e das APIs.

No PowerShell, caso ainda não tenha um `.env`:

```powershell
Copy-Item .env.example .env
```

Não envie o arquivo `.env` ao Git.

### 2. Inicie o projeto

Abra o Docker Desktop e aguarde a inicialização.

No terminal, entre na pasta `Back-end`, onde está o `compose.yaml`,
e execute:

```powershell
docker compose up --build -d
```

Na primeira execução, a construção pode demorar para baixar
as imagens e as dependências. Aguarde também as APIs iniciarem.

Confira os serviços:

```powershell
docker compose ps -a
```

### 3. Acesse o sistema

- Site: http://localhost:8080
- API de alunos e planos: http://localhost:8081
- API de professores, aulas, matrículas e check-in: http://localhost:8082

As consultas são públicas. Para cadastrar, editar ou excluir,
informe no formulário as credenciais configuradas no `.env`:

- Alunos e planos: `ALUNOS_API_USERNAME` e `ALUNOS_API_PASSWORD`.
- Professores, aulas, matrículas e check-in: `AULAS_API_USERNAME` e `AULAS_API_PASSWORD`.

Em uma instalação nova, cadastre primeiro os planos, alunos,
professores e aulas. Depois, cadastre as matrículas e registre check-ins.

### 4. Pare ou retome o projeto

Para parar sem apagar os dados:

```powershell
docker compose stop
```

Para iniciar novamente:

```powershell
docker compose up -d
```

Atenção: `docker compose down -v` remove os volumes do projeto,
apagando os cadastros dos bancos e os eventos armazenados no Kafka.

### Desenvolvimento local do front-end

Para trabalhar na interface com atualização automática ao salvar,
entre na pasta `Front-end` e execute:

```powershell
npm.cmd install
npm.cmd run dev
```

Abra o endereço indicado no terminal, normalmente
http://localhost:5173.

Para consultar e alterar dados reais, mantenha as APIs rodando no Docker.
Use `Ctrl + C` para parar o servidor de desenvolvimento.

### Autenticação

As requisições GET são públicas.
Para ter acesso às demais requisições, requer autenticação HTTP Basic.

No Postman, selecione Authorization -> Basic Auth.
Preencha em usuário e senha o que foi configurado no `.env` para a API que está testando.

Lembrando que o arquivo `.env` não deve ser enviado ao git.

## Demonstração

Em alunos-service ou no site: 

- Cadastre um plano;
- Cadastre um aluno associado ao plano.

No powershell(após executar):

docker compose logs -f --tail=100 notificacoes-service

Em aula-service ou no site:

- Cadastre um professor;
- Cadastre uma aula associada ao professor;
- Faça uma matrícula (idAluno, idAula);
- Verifique o log no powershell.

Exemplo do corpo do post  em `/matriculas`:

```JSON
{
"idAluno": 1,
"idAula": 5
}
```

Os valores dos IDs devem ser existentes(cadastrados por você anteriormente).

O processo também registra durante uma interrupção, então caso faça uma matrícula enquanto o consumidor estiver fechado:

```powershell
docker compose stop notificacoes-service
```
A matrícula aparecerá no log ao reabrir:

```powershell
docker compose start notificacoes-service
docker compose logs -l --tail=100 notificacoes-service
```

O consumidor vai processar esse evento contanto que ele ainda esteja retido no kafka.

## Testes Automatizados

Abra o terminal PowerShell na pasta principal do projeto:

```powershell
cd.\aula-service\aula-service
.\mvnw.cmd "-Dtest=MatriculasServiceTest,MatriculasControllerTest" test
```

Esses testes verificam:

- Cadastro de matrícula com vaga disponível.
- Bloqueio de matrícula duplicada.
- Bloqueio quando aluno ou aula não existe.
- Bloqueio de matrícula e reativação em aula lotada.
- Reativação com vaga disponível.
- Cancelamento preservando IDs e data de inscrição.
- Validação dos dados recebidos pelo controller.
- Respostas HTTP e regras de acesso com e sem autenticação.

Esses testes utilizam valores simulados, então não necessitam de banco de dados e kafka.

## Comandos úteis

Execute na pasta que contém o `compose.yaml`.

Acompanhar os logs:

```powershell
docker compose logs -f --tail=100
```

Parar os serviços:

```powershell
docker compose stop
```

Iniciar os serviços novamente:

```powershell
docker compose up -d
```

Reconstruir uma API específica após alterar seu código:

```powershell
docker compose up --build -d --no-deps nome-da-api
```

Os bancos utilizam volumes do Docker para manter os cadastros.

O comando `docker compose down -v` remove os volumes do projeto e apaga os dados armazenados.

## Limitações e próximas melhorias

- As notificações são simuladas nos logs, não há envio de e-mail;
- A gravação da matrícula e a publicação no Kafka não são atômicas;
- A verificação de vagas ainda precisa de controle de concorrência para
  impedir excesso de matrículas em requisições simultâneas;
- Os testes de integração com PostgreSQL e Kafka ainda não foram automatizados.