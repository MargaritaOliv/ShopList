package com.margaritaolivera.compras.features.auth.data.repository

import com.margaritaolivera.compras.core.network.ApiClient
import com.margaritaolivera.compras.core.network.TokenManager
import com.margaritaolivera.compras.features.auth.data.remote.*
import com.margaritaolivera.compras.features.auth.domain.model.User
import com.margaritaolivera.compras.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiClient,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.token != null && response.user != null) {
                tokenManager.saveSession(
                    token = response.token,
                    userId = response.user.id,
                    userName = response.user.displayName,
                    userEmail = email,
                    userAvatar = response.user.avatarUrl
                )
                Result.success(User(response.user.id, response.user.displayName, response.token))
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, pass: String, name: String): Result<Boolean> {
        return try {
            api.register(RegisterRequest(email, pass, name))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String, newPass: String): Result<Boolean> {
        return try {
            api.resetPassword(ResetPasswordRequest(email, newPass))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(id: String, name: String, email: String, avatar: String?): Result<Boolean> {
        return try {
            api.updateProfile(id, UpdateProfileRequest(name, email, avatar))
            tokenManager.saveSession(
                token = tokenManager.getToken() ?: "",
                userId = id,
                userName = name,
                userEmail = email,
                userAvatar = avatar
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(id: String): Result<Boolean> {
        return try {
            api.deleteAccount(id)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}