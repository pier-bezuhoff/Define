package com.pierbezuhoff.define.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pierbezuhoff.define.Greeting
import com.pierbezuhoff.define.ui.theme.DefineTheme

@Composable
fun MainScreen() {
    // setting: query variants
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    DefineTheme {
        MainScreen()
    }
}