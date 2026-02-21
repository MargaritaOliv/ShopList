package com.margaritaolivera.compras.features.lists.domain.usecase

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import javax.inject.Inject

class CreateListUseCase @Inject constructor(
    private val repository: ListRepository
) {
    suspend operator fun invoke(name: String, ownerId: String): Result<ShoppingList> {
        return repository.createList(name, ownerId)
    }
}