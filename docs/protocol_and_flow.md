# 発動するプロトコルとフロー

response_type と scope の値に応じて次の表の通りにプロトコル（OAuth 2.0 / OIDC）とフロー（Authorization Code / Implicit / Hybrid）を発動する。
response_type が"code token"もしくは id_token を含み、かつ scope が openid を含まない場合、動作が未定義のためエラーとして処理する。

| response_type \ scope | openid + その他 scope   | openid のみ             | openid なし                  |
| --------------------- | ----------------------- | ----------------------- | ---------------------------- |
| token                 | OAuth 2.0 Implicit      | OAuth 2.0 Implicit      | OAuth 2.0 Implicit           |
| code                  | OIDC Authorization Code | OIDC Authorication Code | OAuth 2.0 Authorization Code |
| id_token              | OIDC Implicit           | OIDC Implicit           | invalid_scope                |
| id_token token        | OIDC Implicit           | OIDC Hybrid             | invalid_scope                |
| code token            | OIDC Hybrid             | OIDC Hybrid             | invalid_scope                |
| code id_token         | OIDC Hybrid             | OIDC Hybrid             | invalid_scope                |
| code id_token token   | OIDC Hybrid             | OIDC Hybrid             | invalid_scope                |
