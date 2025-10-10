package com.pierbezuhoff.define.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pierbezuhoff.define.data.QueryVariant

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
            val queryVariants: List<QueryVariant> by viewModel.queryVariants.collectAsStateWithLifecycle()
            SettingsScreen(
                queryVariants = queryVariants,
                saveSettings = { variants ->
                    viewModel.updateQueryVariants(variants)
                },
                goBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}