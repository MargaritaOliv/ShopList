package com.margaritaolivera.compras.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.margaritaolivera.compras.features.auth.presentation.screens.*
import com.margaritaolivera.compras.features.auth.presentation.viewmodels.AuthViewModel
import com.margaritaolivera.compras.features.lists.presentation.screens.HomeScreen
import com.margaritaolivera.compras.features.lists.presentation.screens.ListDetailScreen
import com.margaritaolivera.compras.features.lists.presentation.viewmodels.HomeViewModel
import com.margaritaolivera.compras.features.lists.presentation.viewmodels.ListDetailViewModel

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
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
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
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            val authViewModel: AuthViewModel = hiltViewModel()
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToListDetail = { listId ->
                    navController.navigate("detail/$listId")
                },
                onNavigateToProfile = { navController.navigate("profile") } // ACTIVADO
            )
        }

        composable("profile") {
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "detail/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val detailViewModel: ListDetailViewModel = hiltViewModel()

            ListDetailScreen(
                listId = listId,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}