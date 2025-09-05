package com.pierbezuhoff.define.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.pierbezuhoff.define.data.GOOGLE_DEFINE_QUERY
import com.pierbezuhoff.define.data.Query
import com.pierbezuhoff.define.data.QueryVariant
import com.pierbezuhoff.define.data.loadQueryHistoryFile
import com.pierbezuhoff.define.data.loadQueryVariantsFile
import com.pierbezuhoff.define.data.saveQueryHistoryFile
import com.pierbezuhoff.define.data.saveQueryVariantsFile

class DefineViewModel(
    private val applicationContext: Context,
) : ViewModel() {
    private var fileLoadingIsDone = false

    var queryVariants: List<QueryVariant> by mutableStateOf(listOf(
        GOOGLE_DEFINE_QUERY
    ))
    var queryHistory: List<Query> by mutableStateOf(listOf())

    // bad name...
    // TODO: coroutines
    fun loadInitialDataFromDisk() {
        if (!fileLoadingIsDone) {
            loadQueryVariantsFile(applicationContext, QUERY_VARIANTS_FILENAME)
                .onSuccess { queryVariants = it }
            loadQueryHistoryFile(applicationContext, QUERY_HISTORY_FILENAME, queryVariants.size)
                .onSuccess { queryHistory = it }
            fileLoadingIsDone = true
        }
    }

    fun persistDataToDisk() {
        saveQueryVariantsFile(applicationContext, QUERY_VARIANTS_FILENAME, queryVariants)
        saveQueryHistoryFile(applicationContext, QUERY_HISTORY_FILENAME, queryHistory)
    }

    // good new unique color default each time (eg hue % 30 in okhsl)
    fun createNewQueryVariant(queryVariant: QueryVariant) {
        queryVariants += queryVariant
    }

    fun recordNewQuery(query: Query) {
        // sus performance
        queryHistory = listOf(query) + queryHistory
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
        const val QUERY_VARIANTS_FILENAME = "query-variants.list"
        const val QUERY_HISTORY_FILENAME = "query-history.list"
    }
}