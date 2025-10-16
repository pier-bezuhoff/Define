package com.pierbezuhoff.define.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pierbezuhoff.define.R
import com.pierbezuhoff.define.data.QueryVariant
import kotlinx.serialization.Serializable

@Serializable
object SettingsScreen

@Composable
fun SettingsScreenRoot(
    viewModel: DefineViewModel,
    goBack: () -> Unit,
) {
    val queryVariants by viewModel.queryVariants.collectAsStateWithLifecycle()
    SettingsScreen(
        queryVariants = queryVariants,
        updateQueryVariantAt = viewModel::updateQueryVariantAt,
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    queryVariants: List<QueryVariant>,
    updateQueryVariantAt: (index: Int, newQueryVariant: QueryVariant) -> Unit,
    goBack: () -> Unit,
) {
    val colorColumnWeight = 0.1f
    val nameColumnWeight = 0.3f
    val templateColumnWeight = 0.6f
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(buildAnnotatedString {
                        append("Customize ")
                        withStyle(
                            SpanStyle(color = MaterialTheme.colorScheme.primary)
                        ) {
                            append("Search Templates")
                        }
                    })
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            goBack()
                        }
                    ) {
                        Icon(painterResource(R.drawable.back), "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(Modifier.padding(paddingValues)) {
            // Grid: Color | Name | Template
            Column {
                Row(Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(colorColumnWeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Color")
                    }
                    Column(Modifier.weight(nameColumnWeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Name")
                    }
                    Column(Modifier.weight(templateColumnWeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Template")
                    }
                }
                HorizontalDivider()
                queryVariants.forEachIndexed { index, variant ->
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                updateQueryVariantAt(
                                    index,
                                    // TODO: add color picker w/ alpha
                                    variant.copy(color = Color.Green)
                                )
                            },
                            modifier =
                                Modifier.weight(colorColumnWeight)
                            ,
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                contentColor = variant.color
                            )
                        ) {
                            Icon(painterResource(R.drawable.stroke_circle), "Change color")
                        }
                        TextField(
                            value = variant.name,
                            onValueChange = { newName ->
                                updateQueryVariantAt(
                                    index,
                                    variant.copy(name = newName)
                                )
                            },
                            modifier = Modifier
                                .weight(nameColumnWeight)
                                .padding(4.dp)
                            ,
                        )
                        TextField(
                            value = variant.template,
                            onValueChange = { newTemplate ->
                                updateQueryVariantAt(
                                    index,
                                    variant.copy(template = newTemplate)
                                )
                            },
                            modifier = Modifier
                                .weight(templateColumnWeight)
                                .padding(4.dp)
                            ,
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        queryVariants = listOf(
            QueryVariant.GOOGLE_DEFINE,
            QueryVariant.GOOGLE_DEFINE.copy(name = "a"),
            QueryVariant.GOOGLE_DEFINE,
            QueryVariant.GOOGLE_DEFINE.copy(template = "smol$"),
        ),
        updateQueryVariantAt = { _, _ -> },
        goBack = {}
    )
}