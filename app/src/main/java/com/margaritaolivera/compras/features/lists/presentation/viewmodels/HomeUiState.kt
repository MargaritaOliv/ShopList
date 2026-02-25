package com.margaritaolivera.compras.features.lists.presentation.viewmodels

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.model.Invitation

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userAvatar: String? = null,
    val lists: List<ShoppingList> = emptyList(),
    val invitations: List<Invitation> = emptyList(),
    val error: String? = null
)