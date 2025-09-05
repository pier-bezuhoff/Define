package com.pierbezuhoff.define.data

import android.content.Context

data class Query(
    val queryVariantIndex: Int,
    val content: String,
)

// query history is stored as \n-separated list of
// `<queryVariantIndex> <content>`
// in recent-to-oldest order
fun loadQueryHistoryFile(
    applicationContext: Context,
    filename: String,
    queryVariantsSize: Int,
): Result<List<Query>> =
    runCatching {
        applicationContext.openFileInput(filename)
            .bufferedReader()
            .useLines { lines ->
                return@useLines lines.mapNotNull { line ->
                    val separatorIndex = line.indexOf(' ')
                    // maybe monad doko
                    if (separatorIndex == -1)
                        return@mapNotNull null
                    val variantIndex = line.take(separatorIndex).toIntOrNull()
                    if (variantIndex != null && variantIndex < queryVariantsSize) {
                        val content = line.drop(separatorIndex + 1)
                        Query(variantIndex, content)
                    } else null
                }.toList()
            }
    }.onFailure {
        // triggers on first install
        it.printStackTrace()
    }

// ideally: we only need to prepend new queries most of the time
fun saveQueryHistoryFile(
    applicationContext: Context,
    filename: String,
    queryHistory: List<Query>
): Result<Unit> = runCatching {
    // automatically creates a new file if needed
    applicationContext.openFileOutput(filename, Context.MODE_PRIVATE)
        .bufferedWriter()
        .use {
            queryHistory.forEach { query ->
                it.write(query.queryVariantIndex)
                it.write(" ")
                it.write(query.content)
                it.write("\n")
            }
        }
}.onFailure { it.printStackTrace() }
