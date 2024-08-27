package com.nuzchpt.scrumpoker

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.nuzchpt.scrumpoker.ui.navigation.SetupNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        SetupNavGraph(
            navController = navController,
        )
    }
}

