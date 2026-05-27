package com.example.SuaraRasa_A209008.screens

import android.net.Uri
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.SuaraRasa_A209008.data.PlaylistDataStore
import com.example.SuaraRasa_A209008.PlaylistViewModel

// ... import lainnya ...
import androidx.navigation.NavController // Pastikan ini diimpor
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation // Untuk nested navigation
import com.example.SuaraRasa_A209008.navigation.AppDestinations

// Hapus NavHost dari sini, SoulBeatsApp akan menjadi bagian dari NavGraph utama
// @Composable // Tidak lagi menjadi Composable root untuk NavHost sendiri
// fun SoulBeatsApp() { ... }

// Ganti SoulBeatsApp() menjadi fungsi ekstensi pada NavGraphBuilder
// atau biarkan SoulBeatsHomeScreen menjadi entry point langsung
// dan definisikan route lainnya sebagai bagian dari nested graph.

// Opsi 1: Jadikan SoulBeatsHomeScreen sebagai entry point dan route lainnya sebagai bagian dari nested graph
// (Ini yang paling umum dan bersih)
fun NavGraphBuilder.soulBeatsGraph(navController: NavController) {
    navigation(startDestination = "soulbeats_home", route = AppDestinations.MUSIC_ROUTE) {
        composable("soulbeats_home") {
            val context = LocalContext.current
            val dataStore = remember { PlaylistDataStore(context) }
            val viewModel: PlaylistViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return PlaylistViewModel(dataStore) as T
                    }
                }
            )


            SoulBeatsHomeScreen(
                navController = navController,
                onInstrumentalClick = { navController.navigate("instrumental") },
                onLofiClick = { navController.navigate("lofi") },
                onLullabyClick = { navController.navigate("lullaby") },
                onMeditationClick = { navController.navigate("meditation") },
                onSadClick = { navController.navigate("sad") },
                onSleepClick = { navController.navigate("sleep1") },
                onAddBeatsClick = { navController.navigate("addbeats") },
                onPlaylistClick = { playlist ->
                    navController.navigate(
                        "playlist/${
                            Uri.encode(
                                playlist.name
                            )
                        }"
                    )
                },
                onEditPlaylist = { playlist -> navController.navigate("editbeats/${playlist.id}") },
                playlistViewModel = viewModel
            )
        }

        composable("instrumental") { InstrumentalListScreen(onBackClick = { navController.popBackStack() }) }
        composable("lofi") { LofiListScreen(onBackClick = { navController.popBackStack() }) }
        composable("lullaby") { LullabyListScreen(onBackClick = { navController.popBackStack() }) }
        composable("meditation") { MeditationListScreen(onBackClick = { navController.popBackStack() }) }
        composable("sad") { SadListScreen(onBackClick = { navController.popBackStack() }) }
        composable("sleep1") { SleepListScreen(onBackClick = { navController.popBackStack() }) }

        composable("addbeats") {
            val context = LocalContext.current
            val dataStore = remember { PlaylistDataStore(context) }
            val viewModel: PlaylistViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return PlaylistViewModel(dataStore) as T
                    }
                }
            )
            AddBeatsScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { playlist ->
                    viewModel.addPlaylist(
                        playlist.copy(imageUriString = playlist.imageUri?.toString())
                    )
                    navController.popBackStack()
                },
                playlistToEdit = null
            )
        }

        composable(
            "editbeats/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val dataStore = remember { PlaylistDataStore(context) }
            val viewModel: PlaylistViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return PlaylistViewModel(dataStore) as T
                    }
                }
            )

            val playlistId = backStackEntry.arguments?.getString("playlistId")
            val playlist = viewModel.playlists.find { it.id == playlistId }

            AddBeatsScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { updatedPlaylist ->
                    playlist?.let {
                        viewModel.updatePlaylist(
                            updatedPlaylist.copy(
                                id = it.id,
                                imageUriString = updatedPlaylist.imageUri?.toString()
                            )
                        )
                    }
                    navController.popBackStack()
                },
                playlistToEdit = playlist
            )
        }

        composable(
            "playlist/{playlistName}",
            arguments = listOf(navArgument("playlistName") { type = NavType.StringType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val dataStore = remember { PlaylistDataStore(context) }
            val viewModel: PlaylistViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return PlaylistViewModel(dataStore) as T
                    }
                }
            )

            val playlistName = Uri.decode(backStackEntry.arguments?.getString("playlistName"))
            val playlist = viewModel.playlists.find { it.name == playlistName }

            playlist?.let {
                PlaylistScreen(
                    playlist = it,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

