package com.margaritaolivera.compras.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.margaritaolivera.compras.features.auth.presentation.screens.LoginScreen
import com.margaritaolivera.compras.features.auth.presentation.screens.RegisterScreen
import com.margaritaolivera.compras.features.auth.presentation.viewmodels.AuthViewModel
import com.margaritaolivera.compras.features.lists.presentation.screens.HomeScreen
import com.margaritaolivera.compras.features.lists.presentation.viewmodels.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()

            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = hiltViewModel()

            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToListDetail = { listId ->
                }
            )

        }
    }
}