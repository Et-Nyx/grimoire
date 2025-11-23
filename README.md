# Grimoire - Instruções de Execução

## Servidor (API)
```bash
nix-shell --run "mvn spring-boot:run"
```
O servidor estará disponível em `http://localhost:8080`

## Cliente (TUI)
Em outro terminal:
```bash
nix-shell --run "java -jar target/grimoire-cli-0.0.1-SNAPSHOT-client.jar"
```

Ou diretamente (sem nix-shell):
```bash
java -jar target/grimoire-cli-0.0.1-SNAPSHOT-client.jar
```


## Funcionalidades Implementadas

### Servidor
- `GET /` - Mensagem de boas-vindas
- `GET /sheet/{id}` - Buscar ficha por ID
- `POST /sheet` - Criar/atualizar ficha
- `POST /campaign/notes` - Adicionar nota de campanha
- `GET /campaign/notes/{id}` - Buscar nota por ID

### Cliente TUI
- Menu principal com navegação por setas
- Visualizar fichas existentes
- Criar novas fichas
- Interface colorida no terminal

## Estrutura de Dados
As fichas são salvas em JSON em `data/sheets/`
