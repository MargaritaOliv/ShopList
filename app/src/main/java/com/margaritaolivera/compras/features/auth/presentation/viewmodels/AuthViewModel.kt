package com.margaritaolivera.compras.features.auth.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.margaritaolivera.compras.core.network.TokenManager
import com.margaritaolivera.compras.features.auth.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCurrentUser() {
        viewModelScope.launch {
            val id = tokenManager.getUserId() ?: ""
            val name = tokenManager.getUserName() ?: ""
            val email = tokenManager.getUserEmail() ?: ""
            val avatar = tokenManager.getUserAvatar() ?: ""

            _uiState.update { it.copy(
                userId = id,
                userName = name,
                userEmail = email,
                userAvatar = avatar
            ) }
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            loginUseCase(email, pass).fold(
                onSuccess = {
                    val id = tokenManager.getUserId() ?: ""
                    val name = tokenManager.getUserName() ?: ""
                    val avatar = tokenManager.getUserAvatar() ?: ""

                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = id,
                        userName = name,
                        userEmail = email,
                        userAvatar = avatar
                    ) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Credenciales inválidas o error de red") }
                }
            )
        }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            registerUseCase(email, pass, name).fold(
                onSuccess = {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Usuario registrado correctamente"
                    ) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al registrarse") }
                }
            )
        }
    }

    fun resetPassword(email: String, newPass: String) {
        if (email.isBlank() || newPass.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            resetPasswordUseCase(email, newPass).fold(
                onSuccess = {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Contraseña actualizada correctamente"
                    ) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "Error de red") }
                }
            )
        }
    }

    fun updateProfile(id: String, name: String, email: String, avatar: String?) {
        if (id.isBlank()) {
            _uiState.update { it.copy(error = "Error: ID de usuario no encontrado") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        if (avatar == null || avatar.startsWith("http")) {
            executeServerUpdate(id, name, email, avatar)
            return
        }

        val uri = Uri.parse(avatar)
        MediaManager.get().upload(uri)
            .unsigned("perfil_compras")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String
                    executeServerUpdate(id, name, email, imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    _uiState.update { it.copy(isLoading = false, error = "Error al subir imagen") }
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    private fun executeServerUpdate(id: String, name: String, email: String, avatarUrl: String?) {
        viewModelScope.launch {
            updateProfileUseCase(id, name, email, avatarUrl).fold(
                onSuccess = {
                    val currentToken = tokenManager.getToken() ?: ""
                    tokenManager.saveSession(
                        token = currentToken,
                        userId = id,
                        userName = name,
                        userEmail = email,
                        userAvatar = avatarUrl
                    )

                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        userName = name,
                        userEmail = email,
                        userAvatar = avatarUrl,
                        successMessage = "Perfil actualizado correctamente"
                    ) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "Error al actualizar el perfil") }
                }
            )
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearSession()
            _uiState.update { AuthUiState() }
            onLogoutSuccess()
        }
    }

    fun deleteAccount(id: String, onAccountDeleted: () -> Unit) {
        if (id.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            deleteAccountUseCase(id)
            tokenManager.clearSession()
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onAccountDeleted()
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}