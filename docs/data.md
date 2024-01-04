# Tables

## authorization_codes

| name       | attribute             | description                          |
|------------|-----------------------|--------------------------------------|
| code       | primary key           | Authorization Code                   |
| user_id    | References users.id   | User ID                              |
| client_id  | References clients.id | Client ID                            |
| scope      | NotNull               | Scope                                |
| expiration | NotNull               | Expiration of the authorization code |

## clients

| name          | attribute   | description                   |
|---------------|-------------|-------------------------------|
| id            | primary key | Client ID                     |
| secret        |             | Client Secret                 |
| redirect_uris |             | space-separated redirect URIs |

## users

| name      | attribute       | description     |
|-----------|-----------------|-----------------|
| id        | primary key     | User ID         |
| email     | unique, NotNull | User email      |
| password  | NotNull         | Hashed password |
