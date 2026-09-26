package com.card.nest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.card.nest.presentation.addcard.AddCardScreen
import com.card.nest.presentation.details.CardDetailsScreen
import com.card.nest.presentation.editcard.EditCardScreen
import com.card.nest.presentation.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddCard : Screen("add_card")
    object CardDetails : Screen("card_details/{cardId}") {
        fun createRoute(cardId: Long) = "card_details/$cardId"
    }
    object EditCard : Screen("edit_card/{cardId}") {
        fun createRoute(cardId: Long) = "edit_card/$cardId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCardClick = { cardId ->
                    navController.navigate(Screen.CardDetails.createRoute(cardId))
                },
                onAddCard = {
                    navController.navigate(Screen.AddCard.route)
                }
            )
        }
        
        composable(Screen.AddCard.route) {
            AddCardScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.CardDetails.route,
            arguments = listOf(
                navArgument("cardId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: return@composable
            CardDetailsScreen(
                cardId = cardId,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { cardId ->
                    navController.navigate(Screen.EditCard.createRoute(cardId))
                },
                onDelete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.EditCard.route,
            arguments = listOf(
                navArgument("cardId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: return@composable
            EditCardScreen(
                cardId = cardId,
                onBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}