# Grimoire - RPG Manager

Projeto modular para gerenciamento de campanhas de RPG, com arquitetura Cliente-Servidor.

## Estrutura do Projeto (Multi-Modulo)

```
grimoire/
├── common/           # grimoire-common
│   └── src/
├── server/           # grimoire-server
│   └── src/
├── client-core/      # grimoire-client-core
│   └── src/
├── tui/              # grimoire-tui
│   └── src/
├── data/             # Persistencia local
│   ├── campaigns/
│   ├── sessions/
│   ├── sheets/
│   ├── notes/
│   └── users/
├── pom.xml           # POM pai (multi-modulo)
├── shell.nix
├── start-server.sh
└── start-client.sh
```

- **grimoire-common**: Modelos de dados compartilhados e utilitarios.
  - Modelos: User, Campaign, CharacterSheet, Session, SessionNote.
  - Modelos de ficha: Attributes, Status, CombatStats, Attack, Skill, Magic, Spell, Inventory, Item, Abilities.
  - Utilitarios: CharacterCalculationService, CharacterClassData, CharacterRaceData, ValidationResult.
- **grimoire-server**: Servidor Spring Boot (API REST).
  - Controllers: AuthController, CampaignController, CharacterSheetController, SessionController, HomeController.
  - Repositories: UserRepository, CampaignRepository, CharacterSheetRepository, SessionRepository, SessionNoteRepository.
  - Persistencia em JSON via JsonPersistenceService.
- **grimoire-client-core**: Logica de negocio do cliente (reutilizavel).
  - Services: ApiClient, AuthService, CampaignService, SessionService, SheetService.
  - Client HTTP: GrimoireHttpClient.
  - Exceptions: GrimoireApiException, NotFoundException, UnauthorizedException, ConflictException, ValidationException.
- **grimoire-tui**: Interface de Texto (Terminal) usando Lanterna.
  - Windows: LoginWindow, RegisterWindow, MainMenuWindow, CampaignWindow, CampaignCreateWindow, CampaignJoinWindow, SessionCreateWindow, SessionDetailsWindow, CharacterSheetWindow, UserProfileWindow.
  - Componentes: SessionTimeline, RichTimelineEntry, SocialCard, WrappedTextBox.
  - Estilos: GrimoireTheme.

## Como Executar

### Pre-requisitos
- Java 17+
- Maven (ou use o wrapper/nix-shell)

### Compilacao
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
O servidor estara disponivel em `http://localhost:8080`.

### 2. Iniciar o Cliente (TUI)
Em outro terminal:
```bash
./start-client.sh
```
Ou manualmente:
```bash
java -jar tui/target/grimoire-tui-0.0.1-SNAPSHOT.jar
```

## Funcionalidades

### Autenticacao
- Login e Registro de Usuarios.
- Sessao mantida no cliente.
- Tratamento de erros especificos (credenciais invalidas, usuario nao encontrado, conflito de username).

### Gerenciamento de Campanhas
- **Painel Raiz**: Listar, Criar e Entrar em Campanhas por ID.
- **Painel da Campanha**:
    - Visao do Mestre: Gerenciar campanha, editar configuracoes, deletar campanha.
    - Visao do Jogador: Acessar ficha e notas.
    - Abas: Resumo, Sessoes, Jogadores, Configuracoes.
- Timeline de atividades recentes.

### Gerenciamento de Sessoes
- Criar, visualizar e deletar sessoes de jogo.
- Definir titulo, sumario e data da sessao.
- Navegar pelo historico de sessoes da campanha.

### Sistema de Notas
- Criar notas publicas ou privadas em sessoes.
- Timeline de notas com autor e timestamp.
- Visibilidade: Mestres veem todas as notas, jogadores veem apenas notas publicas e suas proprias notas privadas.

### Fichas de Personagem
- Criacao e Edicao de Fichas completas.
- Sistemas suportados: D&D 5e e Tormenta20.
- Abas organizadas: Principal, Combate, Pericias, Magia, Inventario, Lore.
- Atributos: Forca, Destreza, Constituicao, Inteligencia, Sabedoria, Carisma.
- Status: HP, Mana, Defesa, classe de armadura.
- Combate: ataques, iniciativa, bonus de proficiencia.
- Magia: espacos de magia, lista de magias conhecidas/preparadas.
- Inventario: itens com nome, quantidade, peso e descricao.
- Calculo automatico de modificadores e bonus.
- Word wrap adaptativo em campos de texto.

### Perfil do Usuario
- Visualizar campanhas e fichas do usuario.
- Editar configuracoes da conta.
- Deletar conta.

## Dados
Os dados sao salvos localmente na pasta `data/`:
- `data/users/`: Usuarios cadastrados.
- `data/campaigns/`: Campanhas criadas.
- `data/sessions/`: Sessoes de jogo.
- `data/sheets/`: Fichas de personagens.
- `data/notes/`: Notas de campanha.
