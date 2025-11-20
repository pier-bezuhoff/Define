package com.pierbezuhoff.define.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Composable
fun MainScreenRoot(
    viewModel: DefineViewModel,
    openTab: (url: String) -> Unit,
    goToSettings: () -> Unit,
    quitApp: () -> Unit,
) {
    val initialQueryInput: String by viewModel.initialQueryInput.collectAsStateWithLifecycle()
    val queryVariants: List<QueryVariant> by viewModel.queryVariants.collectAsStateWithLifecycle()
    val queryHistory: List<Query> by viewModel.queryHistory.collectAsStateWithLifecycle()
    val selectedQueryVariantIndex: Int by viewModel.selectedQueryVariantIndex.collectAsStateWithLifecycle()
    val selectedQueryVariant: QueryVariant by viewModel.selectedQueryVariant.collectAsStateWithLifecycle()
    MainScreen(
        queryVariants = queryVariants,
        queryHistory = queryHistory,
        selectedQueryVariantIndex = selectedQueryVariantIndex,
        selectedQueryVariant = selectedQueryVariant,
        initialQueryInput = initialQueryInput,
        recordNewQuery = viewModel::recordNewQuery,
        deleteQueryAt = viewModel::deleteQueryAt,
        changeSelectedQueryVariant = viewModel::changeSelectedQueryVariant,
        dataLoadingJob = viewModel.fileLoadingJob,
        openTab = openTab,
        goToSettings = goToSettings,
        quitApp = quitApp,
    )
}

// MAYBE: add filtering by selected query-variant
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    queryVariants: List<QueryVariant>,
    queryHistory: List<Query>,
    selectedQueryVariantIndex: Int,
    selectedQueryVariant: QueryVariant,
    initialQueryInput: String = "",
    dataLoadingJob: Job? = null,
    recordNewQuery: (Query) -> Unit,
    deleteQueryAt: (index: Int) -> Unit,
    changeSelectedQueryVariant: (newQueryVariantIndex: Int) -> Unit,
    openTab: (url: String) -> Unit,
    goToSettings: () -> Unit,
    quitApp: () -> Unit,
) {
    var queryTFValue by remember {
        mutableStateOf(TextFieldValue(initialQueryInput, TextRange(initialQueryInput.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var queryVariantSelectorIsExpanded by remember { mutableStateOf(false) }

    fun search() {
        val queryContent = queryTFValue.text.trim()
        queryTFValue = TextFieldValue("")
        focusManager.clearFocus()
        if (queryContent.isNotBlank()) {
            val query = Query(queryVariantIndex = selectedQueryVariantIndex, content = queryContent)
            val url = selectedQueryVariant.render(query.content)
            // there is no race condition in ba sing se
            recordNewQuery(query)
            openTab(url)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                        ,
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 4.dp,
                    ) {
                        TextButton(
                            onClick = {
                                queryVariantSelectorIsExpanded = !queryVariantSelectorIsExpanded
                            },
                            modifier = Modifier
                                .padding(4.dp)
                            ,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                text = selectedQueryVariant.name,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        DropdownMenu(
                            expanded = queryVariantSelectorIsExpanded,
                            onDismissRequest = { queryVariantSelectorIsExpanded = false },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            queryVariants.forEachIndexed { index, variant ->
                                DropdownMenuItem(
                                    text = { Text(variant.name) },
                                    onClick = {
                                        queryVariantSelectorIsExpanded = false
                                        changeSelectedQueryVariant(index)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(R.drawable.stroke_circle),
                                            "Search engine",
                                            tint = variant.color,
                                        )
                                    },
                                    colors = MenuDefaults.itemColors().copy(
                                        textColor =
                                            if (selectedQueryVariantIndex == index)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = quitApp,
                    ) {
                        Icon(painterResource(R.drawable.close), "Close the app")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            goToSettings()
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar("open query variant settings")
//                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.settings), "Settings")
                    }
                }
            )
        },
        bottomBar = {
            // query edit field
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
                itemsIndexed(queryHistory) { index, query ->
                    val variant = queryVariants[query.queryVariantIndex]
                    QueryCard(
                        index = index,
                        query = query,
                        setQueryTFValue = { queryTFValue = it },
                        color = variant.color,
                        deleteQueryAt = deleteQueryAt,
                        openQueryTab = {
                            val url = variant.render(query.content)
                            openTab(url)
                        },
                    )
                }
            }
        }
    }
    LaunchedEffect(initialQueryInput, dataLoadingJob) {
        if (initialQueryInput.isNotBlank()) {
            // FIX: race cond with VM.loadInitialDataFromDisk
//            dataLoadingJob?.join()
//            search()
        }
    }
}

@Composable
private fun QueryCard(
    index: Int,
    query: Query,
    setQueryTFValue: (TextFieldValue) -> Unit,
    color: Color,
    deleteQueryAt: (index: Int) -> Unit,
    openQueryTab: () -> Unit,
) {
    Card(
        onClick = {
            // MAYBE: bubble this query to the top of history
            setQueryTFValue(
                TextFieldValue(query.content, TextRange(query.content.length))
            )
            openQueryTab()
        },
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.9f)
        ,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors()
            .copy(containerColor = color)
    ) {
        Row(
            Modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = query.content,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                ,
            )
            Spacer(
                Modifier.weight(1f)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        deleteQueryAt(index)
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    DefineTheme(darkTheme = true) {
        MainScreen(
            queryVariants = listOf(
                QueryVariant.GOOGLE_DEFINE,
                QueryVariant.GOOGLE_DEFINE.copy(name = "Example"),
                QueryVariant.GOOGLE_DEFINE.copy(name = "A longer name for a change"),
            ),
            queryHistory = listOf(
                Query(0, "hi"),
                Query(0, "strange word"),
                Query(0, "a word i forgot"),
                Query(0, "known word in a new context"),
                Query(0, "what?"),
            ),
            selectedQueryVariantIndex = 0,
            selectedQueryVariant = QueryVariant.GOOGLE_DEFINE,
            recordNewQuery = {},
            deleteQueryAt = {},
            changeSelectedQueryVariant = {},
            openTab = {},
            goToSettings = {},
            quitApp = {},
        )
    }
}