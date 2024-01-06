package com.marblet.idp.domain.model

class Client(val clientId: ClientId, val secret: String?, val redirectUris: List<String>)
