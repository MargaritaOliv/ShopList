package com.margaritaolivera.compras.features.lists.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritaolivera.compras.core.network.TokenManager
import com.margaritaolivera.compras.features.lists.domain.usecase.GetUserListsUseCase
import com.margaritaolivera.compras.features.lists.domain.usecase.CreateListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserListsUseCase: GetUserListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserDataAndLists()
    }

    fun loadUserDataAndLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            launch {
                tokenManager.getUserNameFlow().collect { name ->
                    _uiState.update { it.copy(userName = name ?: "Usuario") }
                }
            }

            val userId = tokenManager.getUserId()
            if (userId != null) {
                fetchLists(userId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "No se encontró sesión") }
            }
        }
    }

    private suspend fun fetchLists(userId: String) {
        getUserListsUseCase(userId).fold(
            onSuccess = { myLists ->
                _uiState.update { it.copy(isLoading = false, lists = myLists) }
            },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        )
    }

    fun createNewEvent(name: String) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }

            createListUseCase(name, userId).fold(
                onSuccess = {
                    fetchLists(userId)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}