package com.margaritaolivera.compras.features.auth.presentation.viewmodels

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)