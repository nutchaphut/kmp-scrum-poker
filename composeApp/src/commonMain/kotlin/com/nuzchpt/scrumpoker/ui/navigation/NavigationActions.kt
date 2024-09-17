package com.nuzchpt.scrumpoker.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

class NavigationActions(private val navController: NavController) {

    fun navigateToRoom(roomId: String) {
        navController.navigate(ScreenRoom(roomId = roomId),
            navOptions = navOptions {
                launchSingleTop = true
            }
        )
    }

    fun navigateToMain() {
        navController.navigate(ScreenMain, navOptions = navOptions {
            launchSingleTop = true
        })
    }

}

@Serializable
object ScreenMain

@Serializable
data class ScreenRoom(val roomId: String)
