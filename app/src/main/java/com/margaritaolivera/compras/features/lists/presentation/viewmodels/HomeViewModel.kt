package com.margaritaolivera.compras.features.lists.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritaolivera.compras.core.network.TokenManager
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUserDataAndLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            launch {
                tokenManager.getUserNameFlow().collect { name ->
                    _uiState.update { it.copy(userName = name ?: "Usuario") }
                }
            }

            val avatar = tokenManager.getUserAvatar()
            val userId = tokenManager.getUserId()
            _uiState.update { it.copy(userAvatar = avatar) }

            if (userId != null) {
                fetchLists(userId)
                fetchInvitations(userId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "No se encontró sesión") }
            }
        }
    }

    private suspend fun fetchLists(userId: String) {
        listRepository.getUserLists(userId).fold(
            onSuccess = { myLists ->
                _uiState.update { it.copy(isLoading = false, lists = myLists) }
            },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        )
    }

    private suspend fun fetchInvitations(userId: String) {
        listRepository.getPendingInvitations(userId).fold(
            onSuccess = { invites ->
                _uiState.update { it.copy(invitations = invites) }
            },
            onFailure = { }
        )
    }

    fun respondToInvitation(invitationId: String, accept: Boolean) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }

            listRepository.respondToInvitation(invitationId, userId, accept).fold(
                onSuccess = {
                    fetchInvitations(userId)
                    if (accept) {
                        fetchLists(userId)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al procesar: ${error.message}") }
                }
            )
        }
    }

    fun createNewEvent(name: String) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            listRepository.createList(name, userId).fold(
                onSuccess = { fetchLists(userId) },
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
            )
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            listRepository.deleteList(listId).fold(
                onSuccess = { fetchLists(userId) },
                onFailure = { _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") } }
            )
        }
    }

    fun updateList(listId: String, newName: String) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            listRepository.updateList(listId, newName).fold(
                onSuccess = { fetchLists(userId) },
                onFailure = { _uiState.update { it.copy(isLoading = false, error = "Error al actualizar") } }
            )
        }
    }
}