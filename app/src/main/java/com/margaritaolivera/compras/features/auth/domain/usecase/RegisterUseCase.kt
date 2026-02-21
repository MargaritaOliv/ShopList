package com.margaritaolivera.compras.features.auth.domain.usecase

import com.margaritaolivera.compras.features.auth.domain.repository.AuthRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String, name: String) = repository.register(email, pass, name)
}