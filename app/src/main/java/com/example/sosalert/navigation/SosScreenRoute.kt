package com.example.sosalert.navigation

sealed class SosScreenRoute(val route: String) {
    object Home : SosScreenRoute("home")
    object Contact : SosScreenRoute("contact")
}
