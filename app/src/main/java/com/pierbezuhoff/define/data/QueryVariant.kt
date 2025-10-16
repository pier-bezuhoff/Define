package com.pierbezuhoff.define.data

import android.content.Context
import androidx.compose.ui.graphics.Color

data class QueryVariant(
    /** `$` is to be replaced with query content */
    val template: String,
    val name: String,
    val color: Color,
) {
    init {
//        require(template.any { it == '$' }) {
//            "Template must have a '$' to be substituted with query content"
//        }
        require(name.indexOf(FIELD_SEPARATOR) == -1) {
            "Name must not contain field separator $FIELD_SEPARATOR"
        }
    }

    fun render(content: String): String {
        return template.replace("$", content)
    }

    companion object {
        const val FIELD_SEPARATOR = '\t'
        val GOOGLE_DEFINE = QueryVariant(
            "https://www.google.com/search?q=define%3A$",
            "Define | " + String(byteArrayOf(71, 111, 111, 103, 108, 101), Charsets.US_ASCII),
            Color(0, 100, 200, 120),
        )
        val DEFAULT: QueryVariant = GOOGLE_DEFINE
        val DIDACTIC_EXAMPLE = QueryVariant(
            color = Color(0xFF_F08A5D),
            name = "New name",
            template = "https://define-word-at-dollar-sign.com/search/$",
        )
    }
}

// query variants are stored as \n-separated list of
// `<name>\t<colorULong>\t<template>`
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
                        val firstSeparator = line.indexOf('\t')
                        val secondSeparator = line.indexOf('\t', startIndex = firstSeparator + 1)
                        if (firstSeparator == -1 || secondSeparator == -1) {
                            return@mapNotNull null
                        }
                        val name = line.take(firstSeparator)
                        val colorULong = line.slice((firstSeparator + 1) until secondSeparator)
                            .toULongOrNull()
                        val template = line.drop(secondSeparator + 1)
                        if (colorULong != null && template.isNotBlank())
                            QueryVariant(template, name, Color(colorULong))
                        else null
                    }.toList()
            }
    }.onFailure { e ->
        // triggers on first install
        println("failed to load '$filename'")
        e.printStackTrace()
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
                it.write(queryVariant.name)
                it.write(QueryVariant.FIELD_SEPARATOR.toString())
                it.write(queryVariant.color.value.toString())
                it.write(QueryVariant.FIELD_SEPARATOR.toString())
                it.write(queryVariant.template)
                it.write("\n")
            }
        }
}.onFailure { it.printStackTrace() }
