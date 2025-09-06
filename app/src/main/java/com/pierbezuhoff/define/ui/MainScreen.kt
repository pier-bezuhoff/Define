package com.pierbezuhoff.define.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pierbezuhoff.define.data.Query
import com.pierbezuhoff.define.data.QueryVariant
import com.pierbezuhoff.define.ui.theme.DefineTheme

@Composable
fun MainScreen(
    viewModel: DefineViewModel,
    quitApp: () -> Unit,
) {
    val queryVariants: List<QueryVariant> by
        viewModel.queryVariants.collectAsStateWithLifecycle()
    val queryHistory: List<Query> by
        viewModel.queryHistory.collectAsStateWithLifecycle()
    val selectedQueryVariant: QueryVariant by
        viewModel.selectedQueryVariant.collectAsStateWithLifecycle()
    // setting: query variants
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // query settings
        },
        bottomBar = {
            // query edit field
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // search entered query
                }
            ) {
                // search icon
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        Surface(
            Modifier.padding(innerPadding)
        ) {
        }
        // lazy column
        // bottom input field
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    DefineTheme {
    }
}