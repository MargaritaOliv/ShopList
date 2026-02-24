package com.margaritaolivera.compras.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritaolivera.compras.features.auth.domain.usecase.LoginUseCase
import com.margaritaolivera.compras.features.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            loginUseCase(email, pass).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
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
                    val message = error.message ?: ""
                    val finalError = when {
                        message.contains("409") -> "El correo ya está registrado"
                        message.contains("400") -> "Datos de registro inválidos"
                        else -> "Error al registrarse. Intenta más tarde"
                    }
                    _uiState.update { it.copy(isLoading = false, error = finalError) }
                }
            )
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}