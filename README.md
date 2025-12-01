# Grimoire - RPG Manager

Projeto modular para gerenciamento de campanhas de RPG, com arquitetura Cliente-Servidor.

## Estrutura do Projeto (Multi-Módulo)

- **grimoire-common**: Modelos de dados compartilhados (User, Campaign, CharacterSheet).
- **grimoire-server**: Servidor Spring Boot (API REST).
- **grimoire-client-core**: Lógica de negócio do cliente (reutilizável).
- **grimoire-tui**: Interface de Texto (Terminal) usando Lanterna.

## Como Executar

### Pré-requisitos
- Java 17+
- Maven (ou use o wrapper/nix-shell)

### Compilação
Na raiz do projeto:
```bash
nix-shell --run "mvn clean install"
```

### 1. Iniciar o Servidor
```bash
./start-server.sh
```
Ou manualmente:
```bash
java -jar server/target/grimoire-server-0.0.1-SNAPSHOT.jar
```
O servidor estará disponível em `http://localhost:8080`.

### 2. Iniciar o Cliente (TUI)
Em outro terminal:
```bash
./run-client.sh
```
Ou manualmente:
```bash
java -jar tui/target/grimoire-tui-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Funcionalidades

### Autenticação
- Login e Registro de Usuários.
- Sessão mantida no cliente.

### Gerenciamento de Campanhas
- **Painel Raiz**: Listar, Criar e Entrar em Campanhas.
- **Painel da Campanha**:
    - Visão do Mestre: Gerenciar campanha.
    - Visão do Jogador: Acessar ficha e notas.

### Fichas de Personagem
- Criação e Edição de Fichas (D&D 5e / Tormenta20).
- Persistência em JSON.

## Dados
Os dados são salvos localmente na pasta `data/`:
- `data/users/`: Usuários cadastrados.
- `data/campaigns/`: Campanhas criadas.
- `data/sheets/`: Fichas de personagens.
- `data/notes/`: Notas de campanha.
