package com.pierbezuhoff.define.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pierbezuhoff.define.R
import com.pierbezuhoff.define.data.Query
import com.pierbezuhoff.define.data.QueryVariant
import com.pierbezuhoff.define.ui.theme.DefineTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DefineViewModel,
    openTab: (url: String) -> Unit,
    quitApp: () -> Unit,
) {
    val queryVariants: List<QueryVariant> by
        viewModel.queryVariants.collectAsStateWithLifecycle()
    val queryHistory: List<Query> by
        viewModel.queryHistory.collectAsStateWithLifecycle()
    val selectedQueryVariantIndex: Int by
        viewModel.selectedQueryVariantIndex.collectAsStateWithLifecycle()
    val selectedQueryVariant: QueryVariant by
        viewModel.selectedQueryVariant.collectAsStateWithLifecycle()
    var queryTFValue by remember { mutableStateOf(TextFieldValue("")) }
    // setting: query variants
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun search() {
        val queryContent = queryTFValue.text
        queryTFValue = TextFieldValue("")
        focusManager.clearFocus()
        if (queryContent.isNotBlank()) {
            val query = Query(queryVariantIndex = selectedQueryVariantIndex, content = queryContent)
            val url = selectedQueryVariant.render(query.content)
            // there is no race condition in ba sing se
            viewModel.recordNewQuery(query)
            openTab(url)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = selectedQueryVariant.template,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("open query variant settings")
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.settings), "Settings")
                    }
                }
            )
        },
        bottomBar = {
            // query edit field
            BottomAppBar(
                Modifier.fillMaxWidth()
            ) {
                Surface(Modifier.fillMaxWidth()) {
                    TextField(
                        value = queryTFValue,
                        onValueChange = { queryTFValue = it },
                        modifier = Modifier.focusRequester(focusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { search() }
                        ),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { search() }
            ) {
                Icon(painterResource(R.drawable.search), "Search")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        Surface(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(queryHistory) { query ->
                    val color = queryVariants[query.queryVariantIndex].color
                    // TODO: add delete action
                    Card(
                        onClick = {
                            // MAYBE: bubble this query to the top of history
                            queryTFValue = TextFieldValue(query.content, TextRange(query.content.length))
                            val url = selectedQueryVariant.render(query.content)
                            openTab(url)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(0.9f)
                        ,
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = color)
                    ) {
                        Text(
                            text = query.content,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    DefineTheme {
    }
}