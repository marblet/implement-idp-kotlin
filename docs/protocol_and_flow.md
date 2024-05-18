# 発動するプロトコルとフロー

response_type と scope の値に応じて次の表の通りにプロトコル（OAuth 2.0 / OIDC）とフロー（Authorization Code / Implicit / Hybrid）を発動する。
この IdP は scope="openid"のみの access_token は発行できない仕様とする。
そのため、response_type に token を含み、かつ scope が openid のみの場合、Access Token を発行できないのでエラーとして処理する。
また、response_type に id_token を含み、かつ scope が openid を含まない場合、動作が未定義のためエラーとして処理する。

| response_type \ scope | openid + その他 scope   | openid のみ             | openid なし                  |
| --------------------- | ----------------------- | ----------------------- | ---------------------------- |
| token                 | OAuth 2.0 Implicit      | invalid_scope           | OAuth 2.0 Implicit           |
| code                  | OIDC Authorization Code | OIDC Authorication Code | OAuth 2.0 Authorization Code |
| id_token              | OIDC Implicit           | OIDC Implicit           | invalid_scope                |
| id_token token        | OIDC Implicit           | invalid_scope           | invalid_scope                |
| code token            | OIDC Hybrid             | invalid_scope           | invalid_scope                |
| code id_token         | OIDC Hybrid             | OIDC Hybrid             | invalid_scope                |
| code id_token token   | OIDC Hybrid             | invalid_scope           | invalid_scope                |
