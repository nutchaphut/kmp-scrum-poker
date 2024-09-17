package com.nuzchpt.scrumpoker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nuzchpt.scrumpoker.ui.main.MainScreen
import com.nuzchpt.scrumpoker.ui.room.RoomScreen
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomViewModel
import kotlin.reflect.KClass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: KClass<*> = ScreenMain::class,
    navigationActions: NavigationActions = remember(navController) { NavigationActions(navController) },
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<ScreenMain> {
            MainScreen(navigationActions = navigationActions)
        }
        composable<ScreenRoom> {
            val args = it.toRoute<ScreenRoom>()
            args.roomId.let { roomId ->
                val roomViewModel: RoomViewModel = koinViewModel { parametersOf(roomId) }
                RoomScreen(
                    viewModel = roomViewModel,
                    navigationActions = navigationActions,
                )
            }
        }
    }
}