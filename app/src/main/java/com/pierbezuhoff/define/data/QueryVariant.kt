package com.pierbezuhoff.define.data

import android.content.Context
import androidx.compose.ui.graphics.Color

data class QueryVariant(
    /** `$` is to be replaced with query content */
    val template: String,
    val color: Color,
    // name
) {
    init {
        require(template.any { it == '$' }) {
            "Template must have a '$' to be substituted with query content"
        }
    }

    fun render(content: String): String {
        return template.replace("$", content)
    }

    companion object {
        val GOOGLE_DEFINE = QueryVariant(
            "https://www.google.com/search?q=define%3A$",
            Color(0, 100, 200, 120),
        )
        val DEFAULT: QueryVariant = GOOGLE_DEFINE
    }
}

// query variants are stored as \n-separated list of
// `<colorULong> <template>`
fun loadQueryVariantsFile(
    applicationContext: Context,
    filename: String,
): Result<List<QueryVariant>> =
    runCatching {
        applicationContext.openFileInput(filename)
            .bufferedReader()
            .useLines { lines ->
                return@useLines lines
                    .mapNotNull { line ->
                        val separatorIndex = line.indexOf(' ')
                        if (separatorIndex == -1)
                            return@mapNotNull null
                        val colorULong = line.take(separatorIndex).toULongOrNull()
                        val template = line.drop(separatorIndex + 1)
                        if (colorULong != null && template.isNotBlank())
                            QueryVariant(template, Color(colorULong))
                        else null
                    }.toList()
            }
    }.onFailure {
        // triggers on first install
        it.printStackTrace()
    }

fun saveQueryVariantsFile(
    applicationContext: Context,
    filename: String,
    queryVariants: List<QueryVariant>
): Result<Unit> = runCatching {
    // automatically creates a new file if needed
    applicationContext.openFileOutput(filename, Context.MODE_PRIVATE)
        .bufferedWriter()
        .use {
            queryVariants.forEach { queryVariant ->
                it.write(queryVariant.color.value.toString())
                it.write(" ")
                it.write(queryVariant.template)
                it.write("\n")
            }
        }
}.onFailure { it.printStackTrace() }
