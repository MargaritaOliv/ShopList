package com.margaritaolivera.compras.features.auth.domain.usecase

import com.margaritaolivera.compras.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(id: String, name: String, email: String, avatar: String?) =
        repository.updateProfile(id, name, email, avatar)
}