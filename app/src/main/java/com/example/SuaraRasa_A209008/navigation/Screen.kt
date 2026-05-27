package com.example.SuaraRasa_A209008.navigation


sealed class Screen(val route: String, val title: String) {
    object Compose : Screen("compose", "Write")
    object History : Screen("history", "Past") // Make sure this is here!
}
