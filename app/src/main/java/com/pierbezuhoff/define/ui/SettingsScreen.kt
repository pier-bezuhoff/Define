package com.pierbezuhoff.define.ui

import androidx.compose.runtime.Composable
import com.pierbezuhoff.define.data.QueryVariant
import kotlinx.serialization.Serializable

@Serializable
object SettingsScreen

@Composable
fun SettingsScreen(
    queryVariants: List<QueryVariant>,
    saveSettings: (variants: List<QueryVariant>) -> Unit,
    goBack: () -> Unit,
) {
}