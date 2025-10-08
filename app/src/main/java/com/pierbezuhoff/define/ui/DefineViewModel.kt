package com.pierbezuhoff.define.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pierbezuhoff.define.data.Query
import com.pierbezuhoff.define.data.QueryVariant
import com.pierbezuhoff.define.data.loadQueryHistoryFile
import com.pierbezuhoff.define.data.loadQueryVariantsFile
import com.pierbezuhoff.define.data.saveQueryHistoryFile
import com.pierbezuhoff.define.data.saveQueryVariantsFile
import com.pierbezuhoff.define.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DefineViewModel(
    private val applicationContext: Context,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private var fileLoadingIsDone = false
    private var fileLoadingJob: Job? = null
    private var fileSavingJob: Job? = null

    val queryVariants: MutableStateFlow<List<QueryVariant>> = MutableStateFlow(
        listOf(QueryVariant.DEFAULT)
    )
    val queryHistory: MutableStateFlow<List<Query>> = MutableStateFlow(
        emptyList()
    )
    val selectedQueryVariantIndex: StateFlow<Int> = dataStore.data
        .map { it[QUERY_VARIANT_INDEX] ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)
    val selectedQueryVariant: StateFlow<QueryVariant> =
        selectedQueryVariantIndex.combine(queryVariants) { index, variants ->
            if (index in variants.indices)
                variants[index]
            else
                QueryVariant.DEFAULT
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), QueryVariant.DEFAULT)

    fun loadInitialDataFromDisk() {
        println("loading data...")
        if (!fileLoadingIsDone && fileLoadingJob?.isActive != true) {
            fileLoadingJob = viewModelScope.launch(Dispatchers.IO) {
                var queryVariantsSize = queryVariants.value.size
                loadQueryVariantsFile(applicationContext, QUERY_VARIANTS_FILENAME)
                    .onSuccess { newQueryVariants ->
                        println("loaded qv: $newQueryVariants")
                        queryVariantsSize = newQueryVariants.size
                        viewModelScope.launch {
                            queryVariants.update { newQueryVariants }
                        }
                    }
                loadQueryHistoryFile(applicationContext, QUERY_HISTORY_FILENAME, queryVariantsSize)
                    .onSuccess { newQueryHistory ->
                        println("loaded qh: $newQueryHistory")
                        viewModelScope.launch {
                            queryHistory.update { newQueryHistory }
                        }
                    }
                fileLoadingIsDone = true
                fileLoadingJob = null
                println("data loaded successfully.")
            }
        }
    }

    fun persistDataToDisk() {
        println("persisting data...")
        if (fileSavingJob?.isActive != true) {
            fileSavingJob = viewModelScope.launch(Dispatchers.IO) {
                saveQueryVariantsFile(applicationContext, QUERY_VARIANTS_FILENAME, queryVariants.value)
                saveQueryHistoryFile(applicationContext, QUERY_HISTORY_FILENAME, queryHistory.value)
                fileSavingJob = null
                println("data persisted successfully.")
            }
        }
    }

    // good new unique color default each time (eg hue % 30 in okhsl)
    fun createNewQueryVariant(queryVariant: QueryVariant) {
        queryVariants.update { it + queryVariant }
    }

    fun recordNewQuery(query: Query) {
        // sus performance
        queryHistory.update {
            if (query in it)
                it
            else
                listOf(query) + it
        }
    }

    fun deleteQueryAt(index: Int) {
        queryHistory.update {
            val history = it.toMutableList()
            history.removeAt(index)
            history
        }
    }

    fun changeQueryVariant(newQueryVariantIndex: Int) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[QUERY_VARIANT_INDEX] = newQueryVariantIndex
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        persistDataToDisk()
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
                    application.dataStore,
                ) as T
            }
        }
        private const val QUERY_VARIANTS_FILENAME = "query-variants.list"
        private const val QUERY_HISTORY_FILENAME = "query-history.list"

        private val QUERY_VARIANT_INDEX = intPreferencesKey("queryVariantIndex")
    }
}