package com.nuzchpt.scrumpoker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasource
import com.nuzchpt.scrumpoker.ui.main.MainScreen
import com.nuzchpt.scrumpoker.ui.room.RoomScreen
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String = NavigationDestinations.MAIN_ROUTE,
    navigationActions: NavigationActions = remember(navController) { NavigationActions(navController) },
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavigationDestinations.MAIN_ROUTE) {
            MainScreen(navigationActions = navigationActions)
        }
        composable(route = NavigationDestinations.ROOM_ROUTE) { backStackEntry ->
            backStackEntry.arguments?.getString(ARGS)?.let { roomId ->
                val roomViewModel: RoomViewModel = koinViewModel { parametersOf(roomId) }
                RoomScreen(
                    viewModel = roomViewModel,
                    navigationActions = navigationActions,
                )
            }
        }
    }
}