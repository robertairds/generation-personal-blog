# 📝 Projeto Blog Pessoal

> **Status do Projeto:** 🚀 Concluido & Publicado na Nuvem

O **Blog Pessoal** é uma API RESTful robusta e escalável desenvolvida no ecossistema **Java** e **Spring Boot**. A aplicação gere uma plataforma de blog estruturada, integrando relacionamentos complexos entre entidades, validações rigorosas de dados, testes automatizados e controle de acesso seguro utilizando autenticação via tokens criptográficos JWT.

---

## 🛠️ Tecnologias e Ferramentas Utilizadas

### **Ambiente de Desenvolvimento & IDE**
* ![STS](https://img.shields.io/badge/Spring_Tool_Suite_4-6DB33F?style=for-the-badge&logo=spring&logoColor=white) (**STS / Eclipse** - IDE Principal de Desenvolvimento)

### **Back-end & Linguagem**
* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) (Versão 21)
* ![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) (Versão 3.5.14)
* ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white) (Arquitetura Stateless e Controle Baseado em Filtros)
* ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20Web%20Tokens) (io.jsonwebtoken para Emissão e Validação de Tokens)
* ![Hibernate / JPA](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white) (Persistência Automática e Mapeamento de Tabelas)
* ![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) (Automação de Build e Ciclo de Vida do Projeto)

### **Qualidade de Código & Testes Automatizados**
* ![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=junit5&logoColor=white) (Testes de Integração Automatizados)
* ![Spring Boot Test](https://img.shields.io/badge/Spring_Boot_Test-6DB33F?style=for-the-badge&logo=spring) (TestRestTemplate para simulação de requisições HTTP)

### **Bancos de Dados**
* ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white) (Banco de Dados em Produção - Cloud)
* ![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) (Banco de Dados Local para Desenvolvimento)
* ![H2](https://img.shields.io/badge/H2_Database-003049?style=for-the-badge) (Banco de Dados Relacional em Memória para escopo de Testes)

### **Infraestrutura & DevOps**
* ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) (Empacotamento Multi-stage com Imagem base Eclipse-Temurin JDK)
* ![Render](https://img.shields.io/badge/Render-%2346E3B7.svg?style=for-the-badge&logo=render&logoColor=white) (Hospedagem PaaS da API)
* ![Neon](https://img.shields.io/badge/Neon-00E599?style=for-the-badge&logo=neon&logoColor=black) (Hospedagem Cloud Serverless do Banco PostgreSQL)
* ![Insomnia](https://img.shields.io/badge/Insomnia-black?style=for-the-badge&logo=Insomnia&logoColor=5849BE) (Ferramenta de Cliente HTTP para Testes de Rotas)
* ![Swagger](https://img.shields.io/badge/-Swagger-%23C3E88D?style=for-the-badge&logo=swagger&logoColor=black) (Documentação OpenAPI 3 Interativa)

---

## 🎯 Arquitetura Interna & Estrutura do Código

A API foi modularizada na IDE **STS** seguindo as boas práticas da arquitetura MVC com divisão estrita de responsabilidades:

### 🔒 **Camada de Segurança (`security`)**
* **`SecurityConfig`**: Centraliza os filtros do Spring Security. Desabilita proteção CSRF, define a política de sessão como `STATELESS` e intercepta requisições não autorizadas devolvendo erro HTTP `401 Unauthorized` personalizado.
* **`JwtAuthFilter`**: Interceptador personalizado (`OncePerRequestFilter`) que roda antes do filtro de login padrão. Ele extrai a String contida no Header `Authorization`, decodifica o token e valida o usuário na requisição.
* **`JwtService`**: Responsável pela criptografia pesada (Assinatura baseada em chave HMAC SHA-256). Controla o tempo de expiração dos tokens para 60 minutos.
* **`UserDetailsServiceImpl` & `UserDatailslmpl`**: Fazem a ponte entre as tabelas do banco de dados e as regras de credenciais nativas exigidas pelo Spring Security.

### 👤 **Módulo de Usuários (`/usuarios`)**
Gerenciado via `UsuarioController` e pela classe de serviço dedicada `UsuarioService`:
* **`POST /usuarios/cadastrar`**: Valida a integridade do e-mail e aplica o algoritmo de hash `BCryptPasswordEncoder` com custo de processamento nível 10 antes de salvar o usuário no banco de dados. Evita a duplicação de e-mails usando verificações em tempo real.
* **`POST /usuarios/logar`**: Recebe o payload, desafia o `AuthenticationManager` e retorna o token de acesso injetado no modelo `UsuarioLogin`.
* **`GET /usuarios/all`**: Rota protegida. Lista os metadados dos usuários.
* **`GET /usuarios/{id}`**: Busca as informações cadastrais básicas por ID.
* **`PUT /usuarios/atualizar`**: Atualiza a foto de perfil (limite de 5.000 caracteres no link) e encripta a nova senha informada.

### 🏷️ **Módulo de Temas (`/temas`)**
Mapeado pela entidade `Tema` e seu respectivo `TemaRepository`:
* **`GET /temas`** e **`GET /temas/{id}`**: Listagens e buscas de filtros.
* **`GET /temas/descricao/{descricao}`**: Executa buscas customizadas ignorando caixas altas ou baixas no banco (`ContainingIgnoreCase`).
* **`POST /temas`** e **`PUT /temas`**: Inserção e edição das categorias.
* **`DELETE /temas/{id}`**: Exclusão definitiva de um tema. Possui o comportamento `CascadeType.REMOVE`, excluindo automaticamente todas as postagens vinculadas a ele para evitar registros órfãos no banco de dados.

### 📝 **Módulo de Postagens (`/postagens`)**
Interconecta as pontas das tabelas através de relacionamentos `@ManyToOne`:
* **`GET /postagens`** e **`GET /postagens/{id}`**: Exibição do feed.
* **`GET /postagens/titulo/{titulo}`**: Localização de posts por palavra contida no título.
* **`POST /postagens`** e **`PUT /postagens`**: Salvamento de publicações contendo validações severas de preenchimento (`@NotBlank`), limite de tamanho (5 a 100 caracteres para títulos e 10 a 1000 para texto) e restrição via Regex (`@Pattern`), impedindo que o usuário crie textos ou títulos compostos puramente por numerais.

### 🧪 **Módulo de Testes de Integração (`controller / util`)**
* **`UsuarioControllerTest`**: Bateria de testes automatizados utilizando `@SpringBootTest` com ambiente de porta aleatória (`RANDOM_PORT`), garantindo o isolamento completo da aplicação durante os testes das seguintes regras:
  * Criar e persistir um novo usuário com sucesso.
  * Barrar a duplicação de um usuário já existente.
  * Atualizar os dados cadastrais de um usuário.
  * Listar com sucesso todos os usuários do banco.
* **`JwtHelper` & `TestBuilder`**: Classes utilitárias criadas de forma limpa para desacoplar as requisições de teste. Automatizam a geração de tokens de autorização (`setBearerAuth`) e a montagem rápida de objetos (*Mocks*) de teste.

---

## ⚙️ Isolamento de Ambientes (Multi-profile no STS)

A infraestrutura do código foi projetada no STS para atuar dinamicamente através de múltiplos perfis de propriedades:

1. **Ambiente Local / Desenvolvimento (`dev`):** Ativado através do perfil `application-dev.properties` no STS. Permite que o projeto corra na máquina local conectado ao banco de dados MySQL nativo.
2. **Ambiente Remoto / Produção (`prod`):** Ativado através do `application-prod.properties`. No container Docker hospedado no **Render**, ele intercepta de forma segura as variáveis de ambiente sigilosas (`POSTGRESHOST`, `POSTGRESPORT`, `POSTGRESDATABASE`, etc.) injetadas na plataforma serverless do **Neon**, protegendo os acessos de exposição no GitHub.
3. **Ambiente de Teste:** O Spring ativa automaticamente o banco relacional em memória **H2** ao disparar a classe `UsuarioControllerTest` no JUnit, limpando a base após a execução dos testes e garantindo que os dados simulados nunca alterem os bancos de desenvolvimento ou produção.

---
