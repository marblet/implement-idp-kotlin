# Tables

## authorization_codes

| name         | attribute             | description                          |
|--------------|-----------------------|--------------------------------------|
| code         | primary key           | Authorization Code                   |
| user_id      | References users.id   | User ID                              |
| client_id    | References clients.id | Client ID                            |
| scope        | NotNull               | Scope                                |
| redirect_uri | NotNull               | Redirect URI                         |
| expiration   | NotNull               | Expiration of the authorization code |

## clients

| name          | attribute   | description                   |
|---------------|-------------|-------------------------------|
| id            | primary key | Client ID                     |
| secret        |             | Client Secret                 |
| redirect_uris | NotNull     | space-separated redirect URIs |
| name          | NotNull     | The name of Client            |

## users

| name     | attribute       | description     |
|----------|-----------------|-----------------|
| id       | primary key     | User ID         |
| username | unique, NotNull | User email      |
| password | NotNull         | Hashed password |
