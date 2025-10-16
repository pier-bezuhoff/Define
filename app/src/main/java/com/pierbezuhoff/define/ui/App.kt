package com.pierbezuhoff.define.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App(
    viewModel: DefineViewModel,
    openTab: (url: String) -> Unit,
    quitApp: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MainScreen,
    ) {
        composable<MainScreen> {
            MainScreenRoot(
                viewModel = viewModel,
                openTab = openTab,
                goToSettings = {
                    navController.navigate(SettingsScreen)
                },
                quitApp = quitApp,
            )
        }
        composable<SettingsScreen> {
            SettingsScreenRoot(
                viewModel = viewModel,
                goBack = {
                    navController.navigateUp()
                },
            )
        }
    }
}