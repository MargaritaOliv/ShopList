package com.margaritaolivera.compras.features.lists.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritaolivera.compras.core.network.ApiClient
import com.margaritaolivera.compras.core.network.WebSocketManager
import com.margaritaolivera.compras.features.lists.data.remote.InviteRequest
import com.margaritaolivera.compras.features.lists.data.remote.SocketResponse
import com.margaritaolivera.compras.features.lists.data.remote.UpdateItemRequest
import com.margaritaolivera.compras.features.lists.domain.model.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val apiClient: ApiClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val jsonParser = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    fun connectToWebSocket(listId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val existingItems = apiClient.getItems(listId)
                _uiState.update { it.copy(items = existingItems, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }

        viewModelScope.launch {
            try {
                webSocketManager.connect()
                val subscribeMsg = "{\"type\": \"subscribe\", \"payload\": {\"listId\": \"$listId\"}}"
                webSocketManager.sendMessage(subscribeMsg)

                webSocketManager.messages.collect { json ->
                    handleIncomingMessage(json)
                }
            } catch (e: Exception) { }
        }
    }

    fun inviteUser(email: String, listId: String) {
        if (email.isBlank()) return
        viewModelScope.launch {
            try {
                apiClient.inviteUser(listId, InviteRequest(email))
                _toastMessage.emit("Invitación enviada")
            } catch (e: Exception) {
                _toastMessage.emit("Error al invitar")
            }
        }
    }

    private fun handleIncomingMessage(json: String) {
        try {
            val response = jsonParser.decodeFromString<SocketResponse>(json)
            _uiState.update { currentState ->
                when (response.type) {
                    "item_created" -> {
                        response.item?.let { newItem ->
                            if (currentState.items.any { it.id == newItem.id }) currentState
                            else currentState.copy(items = currentState.items + newItem)
                        } ?: currentState
                    }
                    "item_updated", "item_toggled" -> {
                        response.item?.let { updatedItem ->
                            val newList = currentState.items.map {
                                if (it.id == updatedItem.id) updatedItem else it
                            }
                            currentState.copy(items = newList)
                        } ?: currentState
                    }
                    else -> currentState
                }
            }
        } catch (e: Exception) { }
    }

    fun addItem(title: String, quantity: String, note: String, listId: String) {
        if (title.isBlank()) return
        val message = """
            {
                "type": "create_item",
                "payload": {
                    "listId": "$listId",
                    "title": "$title",
                    "quantity": "$quantity",
                    "note": "$note",
                    "status": "pending"
                }
            }
        """.trimIndent()
        webSocketManager.sendMessage(message)
    }

    fun toggleItemStatus(item: ListItem, isCompleted: Boolean) {
        val newStatus = if (isCompleted) "completed" else "pending"
        val message = """
            {
                "type": "update_item",
                "payload": {
                    "id": "${item.id}",
                    "version": ${item.version},
                    "changes": { "status": "$newStatus" }
                }
            }
        """.trimIndent()
        webSocketManager.sendMessage(message)
    }

    fun updateItemContent(itemId: String, title: String, quantity: String, note: String) {
        viewModelScope.launch {
            try {
                apiClient.updateItem(itemId, UpdateItemRequest(title, quantity, note))
                _toastMessage.emit("Item actualizado")
            } catch (e: Exception) {
                _toastMessage.emit("Error al editar item")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}