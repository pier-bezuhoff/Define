package com.pierbezuhoff.define

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pierbezuhoff.define.ui.DefineViewModel
import com.pierbezuhoff.define.ui.MainScreen
import com.pierbezuhoff.define.ui.buildCustomTabsIntent
import com.pierbezuhoff.define.ui.theme.DefineTheme
import androidx.core.net.toUri
import com.pierbezuhoff.define.ui.App
import com.pierbezuhoff.define.ui.theme.LocalIsDarkTheme
import kotlin.math.roundToInt

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val defineViewModel: DefineViewModel by viewModels { DefineViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DefineTheme() {
                val context = LocalContext.current
                val darkColorScheme = LocalIsDarkTheme.current
                val toolbarColor = MaterialTheme.colorScheme.primary
                App(
                    viewModel = defineViewModel,
                    openTab = { url ->
                        openTab(context, !darkColorScheme, toolbarColor, url)
                    },
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

private fun openTab(
    context: Context,
    lightColorScheme: Boolean,
    toolbarColor: Color,
    url: String,
) {
    val displayHeight = context.resources.displayMetrics.heightPixels
    val height = (0.7*displayHeight).roundToInt()
    val intent = buildCustomTabsIntent(500, lightColorScheme, toolbarColor.toArgb())
    intent.launchUrl(context, url.toUri())
//        intent.intent.data = url.toUri()
//        context.startActivity(intent.intent)
}

