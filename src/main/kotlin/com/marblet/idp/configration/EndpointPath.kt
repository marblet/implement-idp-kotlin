package com.marblet.idp.configration

class EndpointPath {
    companion object {
        const val AUTHORIZE_PATH = "authorize"
        const val LOGIN_PATH = "login"
        const val CONSENT_PATH = "consent"
        const val TOKEN_PATH = "token"
        const val USERINFO_PATH = "userinfo"
        const val OPENID_CONFIGURATION_PATH = ".well-known/openid-configuration"
        const val JWKS_PATH = "jwks"
    }
}
