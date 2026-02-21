package com.margaritaolivera.compras.features.auth.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("display_name") val displayName: String
)

@Serializable
data class AuthResponse(
    val token: String? = null,
    val user: UserDto? = null
)

@Serializable
data class UserDto(
    val id: String,
    @SerialName("display_name") val displayName: String
)