# URLShortenerAPI

API REST para criar URLs curtas e redirecionar para os endereços originais.

## Executar

É necessário ter Java 25, Maven e PostgreSQL instalados. Crie o banco `urlshortener`
e configure a conexão pelas variáveis de ambiente abaixo (os valores entre parênteses
são os padrões locais):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/urlshortener
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

Na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

As tabelas são criadas/atualizadas automaticamente na inicialização.
A configuração do banco também pode ser ajustada em
`src/main/resources/application.yaml`.

A URL pública usada nas respostas pode ser alterada em `app.base-url` no arquivo
`src/main/resources/application.yaml`.

## Endpoints

### Criar URL curta

`POST /shorten`

```json
{
  "url": "https://example.com/minha-pagina"
}
```

Retorna `201 Created` com o identificador, a URL original e a URL curta:

```json
{
  "id": "a1B2c3D",
  "originalUrl": "https://example.com/minha-pagina",
  "shortUrl": "http://localhost:8080/a1B2c3D"
}
```

Somente URLs absolutas com esquema `http` ou `https` são aceitas.

### Redirecionar

`GET /{id}` retorna `302 Found` para a URL original.

### Excluir

`DELETE /{id}` retorna `204 No Content`.

As URLs encurtadas são salvas na tabela `short_urls` do PostgreSQL e permanecem
disponíveis após reiniciar a aplicação.
