package com.pierbezuhoff.define

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pierbezuhoff.define.ui.DefineViewModel
import com.pierbezuhoff.define.ui.MainScreen
import com.pierbezuhoff.define.ui.theme.DefineTheme

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val defineViewModel: DefineViewModel by viewModels { DefineViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DefineTheme {
                MainScreen(
                    viewModel = defineViewModel,
                    quitApp = { finishAndRemoveTask() }
                )
                LaunchedEffect(defineViewModel) {
                    defineViewModel.loadInitialDataFromDisk()
                }
            }
        }
    }

    // onDestroy doesn't proc, idk why (maybe no compat)
    override fun onPause() {
        super.onPause()
        defineViewModel.persistDataToDisk()
    }
}