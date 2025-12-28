package com.example.sosalert.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sosalert.contact.ui.ContactScreen
import com.example.sosalert.sos.ui.SosScreen

@Composable
fun SosAppNavigation(
    modifier: Modifier
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SosScreenRoute.Home.route
    ) {
        composable(SosScreenRoute.Home.route) {
            SosScreen(
                onContact = { navController.navigate(SosScreenRoute.Contact.route) },
                onMessage = { /*TODO*/ }
            )
        }

        composable(SosScreenRoute.Contact.route) {
            ContactScreen(onBack = { navController.popBackStack() })
        }
    }
}
