package com.example.SuaraRasa_A209008.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.SuaraRasa_A209008.ui.theme.EmergencyViewModel

object EmergencyDestinations {
    const val EMERGENCY_GRAPH = "emergency_graph"
    const val HOME = "emergency_graph/home"
    const val ADD = "add"
    const val DETAILS = "details/{contactId}"
}

fun NavGraphBuilder.emergencyGraph(
    navController: NavController,
    viewModel: EmergencyViewModel
)
 {
    navigation(
        startDestination = EmergencyDestinations.HOME,
        route = EmergencyDestinations.EMERGENCY_GRAPH
    ) {
        composable(EmergencyDestinations.HOME) {
            val context = LocalContext.current
            HomeScreen(
                viewModel = viewModel,
                onAddContact = { navController.navigate(EmergencyDestinations.ADD) },
                onContactSelected = { contact ->
                    navController.navigate("details/${contact.id}")
                },
                onCall = { contact ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${contact.phoneNumber}")
                    }
                    context.startActivity(intent)
                }
            )
        }

        composable(EmergencyDestinations.ADD) {
            AddContactScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(EmergencyDestinations.DETAILS) { backStackEntry ->
            val context = LocalContext.current
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            val contact = viewModel.contacts.value.find { it.id == contactId }

            if (contact != null) {
                DetailsScreen(
                    contact = contact,
                    onBack = { navController.popBackStack() },
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${contact.phoneNumber}")
                        }
                        context.startActivity(intent)
                    },
                    onToggleFavorite = { viewModel.toggleFavorite(contact.id) }
                )
            }
        }
    }
}
