package com.margaritaolivera.compras.features.auth.presentation.viewmodels

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,

    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userAvatar: String? = null
)