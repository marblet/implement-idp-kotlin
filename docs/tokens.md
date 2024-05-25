# 各種トークン仕様

## Access Token

Access Token には JWT を採用する。Access Token は IdP が検証することを想定し、署名方式には HMAC を使用する。

### Payload

JWT の Payload に 認可情報を含める。アクセス範囲は"sco" Claim で表現し、アクセスの許可された scope のリストを半角スペース区切りで持たせる。

| key | description                                            |
| --- | ------------------------------------------------------ |
| sub | User ID                                                |
| aud | Client ID                                              |
| iat | Access Token の発行時刻 (unixtime)                     |
| exp | Access Token の有効期限 (unixtime)                     |
| sco | アクセスを許可した scope のリスト (半角スペース区切り) |

## Refresh Token

Refresh Token には JWT を採用する。Refresh Token の検証は IdP 以外で実施されることは想定されないため、署名方式には HMAC を使用する。

### Payload

Access Token と同様に Payload に認可情報を含める。

| key | description                                            |
| --- | ------------------------------------------------------ |
| sub | User ID                                                |
| aud | Client ID                                              |
| iat | Refresh Token の発行時刻 (unixtime)                    |
| exp | Refresh Token の有効期限 (unixtime)                    |
| sco | アクセスを許可した scope のリスト (半角スペース区切り) |

## ID Token

ID Token には JWT を採用する。署名方式には RSA を使用する。

### Payload

| key | description                    |
| --- | ------------------------------ |
| sub | User ID                        |
| aud | Client ID                      |
| iat | ID Token の発行時刻 (unixtime) |
| exp | ID Token の有効期限 (unixtime) |
| iss | 発行者                         |
