# Token Endpoint
## Specification of Access Token
The Access Token is represented as a JWT. The payload includes the user ID, the time which the token issued, expiration time, scope, and Client ID.
Access Tokens are signed using the HS256 algorithm.

### payload
| key  | value           |
|------|-----------------|
| sub  | User ID         |
| iat  | Issued time     |
| exp  | Expiration time |
| aud  | Client ID       |
| sco  | Scopes          |
