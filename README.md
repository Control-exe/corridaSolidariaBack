# API Corrida Solidária

API REST das inscrições da 5ª Corrida Solidária da Vila Esperança. Os dados ficam em memória e a lista começa vazia.

## Como executar

Requisito: JDK 21. O Maven é baixado automaticamente pelo wrapper.

```bash
./mvnw spring-boot:run
```

No Windows: `mvnw.cmd spring-boot:run`. A API sobe em `http://localhost:8080` e é consumida por um
cliente HTTP (Postman, curl), com `Content-Type: application/json`.

Testes: `./mvnw test`

## Rotas

| Operação  | Rota                            | Sucesso | Recusas  |
|-----------|---------------------------------|---------|----------|
| Cadastrar | `POST /api/inscricoes`          | 201     | 400, 409 |
| Listar    | `GET /api/inscricoes`           | 200     | –        |
| Resumo    | `GET /api/inscricoes/resumo`    | 200     | –        |
| Consultar | `GET /api/inscricoes/{email}`   | 200     | 404      |
| Editar    | `PUT /api/inscricoes/{email}`   | 200     | 400, 404 |
| Cancelar  | `DELETE /api/inscricoes/{email}`| 204     | 404      |

Corpo do cadastro (na edição o e-mail vem da URL e o do corpo é ignorado):

```json
{ "nome": "Ana Souza", "email": "ana.souza@email.com", "dataNascimento": "1990-05-20", "percurso": "KM_10" }
```