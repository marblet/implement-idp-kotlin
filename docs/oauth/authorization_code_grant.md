# Authorization Code Grant

Defined in https://datatracker.ietf.org/doc/html/rfc6749

## Obtaining Authorization

| endpoint   | description                                                           |
|------------|-----------------------------------------------------------------------|
| /authorize | Verify the request is valid                                           |
| /login     | Authenticate the resource owner                                       |
| /consent   | Granting or denying the client's access request by the resource owner |

### /authorize
#### Input
The /authorize endpoint receives GET requests with the following query parameters.

| parameter    | required | description                                |
|--------------|----------|--------------------------------------------|
| client_id    | true     |                                            |
| request_type | true     | "code" only                                |
| redirect_uri |          | "application/x-www-form-urlencoded" format |
| scope        |          |                                            |
| state        |          |                                            |

#### Flow

1. Verify request_type (must be "code")
2. Verify client
   - Client exists
   - redirect_uri is valid if redirect_uri exists
   - scope is valid
3. Redirect to `/login`

#### Output

Redirects to the /login endpoint with the following query parameters.

| parameter | required | description                          |
|-----------|----------|--------------------------------------|
| done      | true     | Redirect URI to the consent endpoint |


### /login (GET)
`GET /login` returns the login screen.

### /login (POST)
The authorization server authenticates the resource owner via username and password.

#### Input
The /authorize endpoint receives GET requests with the following query parameters and request body.

The query parameters are:

| parameter | required | description                          |
|-----------|----------|--------------------------------------|
| done      | true     | Redirect URI to the consent endpoint |

The request body contains:

| parameter | required | description |
|-----------|----------|-------------|
| username  | true     |             |
| password  | true     |             |

#### Flow

1. Verify user exists and the password is valid
2. Redirect to `done`

#### Output

Assigns a login cookie and redirects to the URI in the input query parameters.

#### Error Response

Returns a json-formatted error response with Http Status 400 if one or more query parameters are invalid.

### /consent (GET)

`GET /consent` returns the consent screen.

#### Flow

1. Verify whether the user have logged in. If not, redirect to the login page.
2. Show consent page.

### /consent (POST)

`POST /consent` is called if the user grants the client to access the user's resources.

#### Input

The query parameters are:

| parameter    | required | description                                |
|--------------|----------|--------------------------------------------|
| client_id    | true     |                                            |
| request_type | true     | "code" only                                |
| redirect_uri |          | "application/x-www-form-urlencoded" format |
| scope        |          |                                            |
| state        |          |                                            |

The cookie is:

| name  | required | description                                |
|-------|----------|--------------------------------------------|
| login | true     |                                            |

#### Flow

1. Generate an authorization code
2. Save the authorization code data to the authorization_codes table
3. Generate redirect URI by adding the authorization code as `code` and the state as `state` to the redirect_uri

#### Output
Adds code and state as query parameters to `redirect_uri`, and redirects to it.

| parameter | required                                                 | description |
|-----------|----------------------------------------------------------|-------------|
| code      | true                                                     |             |
| state     | true if the "state" parameter was present in the request |             |
