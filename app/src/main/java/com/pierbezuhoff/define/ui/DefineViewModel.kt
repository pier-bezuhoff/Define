package com.pierbezuhoff.define.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import java.io.File
import java.io.FileNotFoundException

class DefineViewModel(
    private val applicationContext: Context,
) : ViewModel() {
    private val queryVariantsFile = File(applicationContext.filesDir, QUERY_VARIANTS_FILENAME)
    private val queryHistoryFile = File(applicationContext.filesDir, QUERY_HISTORY_FILENAME)
    private var fileLoadingIsDone = false

    var queryVariants: List<String> by mutableStateOf(listOf(
        GOOGLE_DEFINE_QUERY
    ))
    var queryHistory: List<String> by mutableStateOf(listOf())

    // bad name...
    fun loadFiles() {
        if (!fileLoadingIsDone) {
            loadQueryVariantsFile()
            loadQueryHistoryFile()
            fileLoadingIsDone = true
        }
    }

    private fun loadQueryVariantsFile() {
        try {
            applicationContext.openFileInput(QUERY_VARIANTS_FILENAME)
                .bufferedReader()
                .useLines { lines ->
                    queryVariants = lines.toList()
                }
        } catch (_: FileNotFoundException) {
            // triggers on first install
            println("No $QUERY_VARIANTS_FILENAME found")
        }
    }

    private fun loadQueryHistoryFile() {
        try {
            applicationContext.openFileInput(QUERY_HISTORY_FILENAME)
                .bufferedReader()
                .useLines { lines ->
                    queryHistory = lines.toList()
                }
        } catch (_: FileNotFoundException) {
            // triggers on first install
            println("No $QUERY_HISTORY_FILENAME found")
        }
    }

    fun persistState() {
        try {
            // automatically creates new file if needed
            applicationContext.openFileOutput(QUERY_VARIANTS_FILENAME, Context.MODE_PRIVATE)
                .bufferedWriter()
                .use {
                    it.write(queryVariants.joinToString("\n"))
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            applicationContext.openFileOutput(QUERY_HISTORY_FILENAME, Context.MODE_PRIVATE)
                .bufferedWriter()
                .use {
                    it.write(queryHistory.joinToString("\n"))
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        // reference: https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                //val savedStateHandle = extras.createSavedStateHandle()
                return DefineViewModel(
                    application.applicationContext,
                ) as T
            }
        }
        const val GOOGLE_DEFINE_QUERY = ""
        // '\n'-separated list
        const val QUERY_VARIANTS_FILENAME = "query-variants.list"
        const val QUERY_HISTORY_FILENAME = "query-history.list"
    }
}