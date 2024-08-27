package com.nuzchpt.scrumpoker.ui.navigation

import androidx.navigation.NavController
import com.nuzchpt.scrumpoker.ui.navigation.NavigationDestinations.ROOM_ROUTE
import com.nuzchpt.scrumpoker.ui.navigation.Screen.MAIN
import com.nuzchpt.scrumpoker.ui.navigation.Screen.PROFILE
import com.nuzchpt.scrumpoker.ui.navigation.Screen.ROOM

object Screen {
    const val MAIN = "MAIN"
    const val PROFILE = "EDIT_PROFILE"
    const val ROOM = "ROOM"
}

object NavigationDestinations {
    const val MAIN_ROUTE = MAIN
    const val PROFILE_ROUTE = PROFILE
    const val ROOM_ROUTE = "$ROOM?$ARGS={$ARGS}"
}

class NavigationActions(private val navController: NavController) {

    fun navigateToRoom(roomId: String) {
        navController.navigate(ROOM_ROUTE.replace("{$ARGS}", roomId))
    }

}

const val ARGS = "args"