# URLShortenerAPI

API REST para criar URLs curtas e redirecionar para os endereços originais.

## Executar

É necessário ter Java 25 e Maven instalados. Na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

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

Os dados ficam em memória e são perdidos quando a aplicação é reiniciada.