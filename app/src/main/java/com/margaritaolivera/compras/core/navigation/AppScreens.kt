package com.margaritaolivera.compras.core.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object Register : AppScreens("register")
    object Home : AppScreens("home")
}