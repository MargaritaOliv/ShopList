package com.margaritaolivera.compras.features.lists.presentation.viewmodels

import com.margaritaolivera.compras.features.lists.domain.model.ListItem

data class ListDetailUiState(
    val isLoading: Boolean = false,
    val eventName: String = "",
    val shareCode: String = "",
    val items: List<ListItem> = emptyList(),
    val error: String? = null
)