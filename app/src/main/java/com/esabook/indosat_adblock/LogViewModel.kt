package com.esabook.indosat_adblock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class LogViewModel() : ViewModel() {
    private val repository: LogRepository by lazy {
        LogRepository(App.db.logDao())
    }
    val logs: Flow<PagingData<LogEntity>> = repository.getLogs()
        .cachedIn(viewModelScope)
}