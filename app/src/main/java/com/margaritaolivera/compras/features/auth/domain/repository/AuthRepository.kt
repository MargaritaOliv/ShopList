package com.margaritaolivera.compras.features.auth.domain.repository

import com.margaritaolivera.compras.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, displayName: String): Result<Boolean>
}