package com.marblet.idp.domain.model

enum class AuthorizationError(val error: String) {
    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    ACCESS_DENIED("access_denied"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    INVALID_SCOPE("invalid_scope"),
    SERVER_ERROR("server_error"),
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable"),
    LOGIN_REQUIRED("login_required"),
    CONSENT_REQUIRED("consent_required"),
}
