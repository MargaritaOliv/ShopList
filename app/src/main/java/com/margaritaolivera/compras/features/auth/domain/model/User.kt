package com.margaritaolivera.compras.features.auth.domain.model

data class User(
    val id: String,
    val displayName: String,
    val token: String
)