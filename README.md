# audit-service

# Serviço de Auditoria - Backend

## Introdução

Este microsserviço faz parte de um sistema modular de gerenciamento de pedidos, focado no registro e persistência de eventos de auditoria.
Ele é responsável por receber, persistir e expor registros de auditoria provenientes de diferentes fontes (HTTP, Kafka, etc.), 
garantindo a disponibilidade e a rastreabilidade dos eventos do sistema.

## Objetivo do Projeto

O objetivo principal deste microsserviço é fornecer uma API e consumidores confiáveis para gerenciar registros de auditoria, 
permitindo operações de consulta e criação de eventos de auditoria, além de persistir de forma durável os eventos recebidos do Kafka ou de chamadas HTTP REST.

## Requisitos do Sistema

Para executar este microsserviço, você precisará dos seguintes requisitos:

- **Sistema Operacional**: Windows, macOS ou Linux
- **Memória RAM**: Pelo menos 4 GB recomendados
- **Espaço em Disco**: Pelo menos 500 MB de espaço livre
- **Software**:
    - Docker e Docker Compose
    - Java JDK 11 ou superior
    - Maven 3.6 ou superior
    - PostgreSQL
    - Git

## Estrutura do Projeto

A estrutura do projeto está organizada da seguinte forma:
```plaintext
audit-service/
│
├── src/
│ └── main/
│   ├── java/
│   │ └── com.fiap.auditservice
│   │   ├── adapter/
│   │   │   ├── web/ : Controladores REST e DTOs ([AuditController]
│   │   │   └── web/mapper/ : MapStruct mappers ([AuditMapper]
│   │   ├── application/
│   │   │   ├── service/ : Implementações de casos de uso ([SaveAuditRecordService]
│   │   │   └── usecase/ : Interfaces de caso de uso ([SaveAuditRecordUseCase]
│   │   ├── domain/ : Entidades de domínio ([AuditRecord]
│   │   ├── domain/port/out/ : Ports para infra ([AuditRepositoryPort]
│   │   ├── infrastructure/
│   │   │   ├── config/ : Configurações (Kafka, Swagger, Security) ([OpenApiConfig]
│   │   │   ├── config/kafka/ : Consumidor Kafka ([AuditKafkaConsumer]
│   │   │   └── persistence/
│   │   │       ├── jpa/ : Entidade JPA ([AuditJpaEntity], Repository Spring Data ([AuditJpaRepository]
│   │   │       ├── mapper/ : Conversor JPA <-> domínio ([AuditEntityMapper]
│   │   │       └── repository/ : Adapter que implementa a porta de persistência ([AuditRepositoryAdapter]
│   │   └── AuditServiceApplication.java : Classe principal da aplicação ([AuditServiceApplication]
│   └── resources/
│       └── application.properties : Configurações da aplicação.
├── pom.xml : Arquivo de configuração do Maven.
├── Dockerfile : Arquivo para construção da imagem Docker.
├── docker-compose.yml : Arquivo para orquestração de contêineres.
└── README.md : Documentação do projeto.
```

## Segurança

A segurança do microsserviço é configurada com Spring Security. Configurações específicas são providas para diferentes perfis.

## Visão Geral do Projeto

Este microsserviço é desenvolvido com Spring Boot e segue uma arquitetura limpa simplificada, separando claramente as responsabilidades entre domínio, persistência, casos de uso e interface.

## Arquitetura

A arquitetura segue o padrão MVC e princípios da Arquitetura Limpa, com camadas bem definidas:

- **Domain**: Representa as entidades de negócio .
- **UseCase**: Serviços que implementam regras de negócio e casos de uso.
- **Gateway/Repository**: Interface e implementação para acesso a dados.
- **Controller**: Exposição dos endpoints REST para interação externa.
- **Mapper**: Conversão entre entidades JPA, domínios e DTOs.

## Princípios de Design e Padrões de Projeto

### Princípios de Design

1. **Single Responsibility Principle (SRP)**: Cada classe tem uma responsabilidade única.
2. **Open/Closed Principle (OCP)**: Facilita extensão sem modificação direta.

### Padrões de Projeto

1. **MVC (Model-View-Controller)**: Controllers REST, Domain model, Responses JSON.
2. **Gateway/Adapter Pattern**: Abstrai acesso a dados via [AuditRepositoryPort].
3. **Mapper Pattern**: MapStruct e mappers manuais para conversões entre camadas.

## Interação entre as Partes do Sistema

1. **Cliente**: Envia requisições HTTP para criação/consulta de registros de auditoria.
2. **Controller**: [AuditController] recebe as requisições e delega para os casos de uso.
3. **UseCase**: [SaveAuditRecordService] aplica regras e define id/createdAt quando necessário.
4. **Gateway/Repository**: [AuditRepositoryAdapter] interage com o banco via JPA ([AuditJpaRepository].
5. **Consumidor Kafka**: [AuditKafkaConsumer] consome tópicos e persiste eventos como auditoria.
6. **Banco de Dados**: Armazena os registros de auditoria em tabela `audits` representada por [AuditJpaEntity].

## Tecnologias Utilizadas

- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **MapStruct**
- **Lombok**
- **Springdoc OpenAPI (Swagger)** 
- **Kafka Client / Spring for Apache Kafka**
- **Docker e Docker Compose**

## Pré-requisitos

Antes de executar o microsserviço, certifique-se de ter instalado:

- Docker e Docker Compose
- Java JDK 11 ou superior
- Maven 3.6 ou superior
- PostgreSQL rodando localmente ou via container

### Passos para Executar o Docker Compose

1. Certifique-se de que o Docker e o Docker Compose estejam instalados e rodando na sua máquina.
2. No terminal, navegue até o diretório onde está localizado o arquivo `docker-compose.yml`.
3. Execute o seguinte comando para iniciar os contêineres:
   ```bash
   docker compose up
   ```
4. A aplicação estará disponível em Swagger em `http://localhost:8084/swagger-ui/index.html#/`.
5. O banco de dados PostgreSQL estará rodando no respectivo serviço do compose (ex.: `postgres`) na porta `5432`.
6. A ferramenta Adminer estará disponível para visualização do banco de dados no endereço `http://localhost:8088` (se configurada no compose).

### Passos para Conectar no Banco de Dados com o Adminer

1. Acesse o endereço `http://localhost:8088`.
2. Em Sistema, escolha PostgreSQL.
3. Em Servidor, preencha o nome do serviço do Postgres do Docker Compose (ex.: `postgres`).
4. Em Usuário, preencha `postgres`.
5. Em Senha, preencha `postgres`.
6. Em Base de dados, preencha com `postgres`.
7. Clique em Entrar.

## Endpoints Principais

- `POST /api/audits` - Criar novo registro de auditoria (via [AuditController](psi_element://com.fiap.auditservice.adapter.web.AuditController)).
- `GET /api/audits` - Listar registros de auditoria (aceita `limit` como query param).
- `GET /api/audits/{id}` - Buscar registro de auditoria por id.
- O consumidor Kafka também cria registros de auditoria automaticamente quando mensagens são consumidas em tópicos configurados ([AuditKafkaConsumer].

## Contribuição

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nome-da-feature`).
3. Faça commit das suas alterações (`git commit -m 'Descrição da feature'`).
4. Envie para o repositório remoto (`git push origin feature/nome-da-feature`).
5. Abra um Pull Request.

## Licença

Este projeto é privado ou não possui licença específica.

## Referências e Recursos

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- MapStruct: https://mapstruct.org
- PostgreSQL: https://www.postgresql.org
- Springdoc OpenAPI: https://springdoc.org


## Conclusão

Este microsserviço de auditoria implementa captura, persistência e exposição de eventos do sistema, com foco em confiabilidade e observabilidade. A arquitetura modular facilita manutenção e integração com outros serviços do ecossistema.

