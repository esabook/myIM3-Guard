package com.esabook.indosat_adblock

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class LogRepository(private val dao: LogDao) {

    fun getLogs(): Flow<PagingData<LogEntity>> {
        return Pager(
            // Konfigurasi Paging
            config = PagingConfig(
                pageSize = 20,          // Jumlah item yang dimuat per halaman
                enablePlaceholders = true, // Tampilkan placeholder untuk item yang belum dimuat
                maxSize = 100           // Jumlah item maksimum dalam cache
            ),
            // PagingSourceFactory: sumber data (dalam hal ini, dari Room DAO)
            pagingSourceFactory = { dao.getAllLogsPagingSource() }
        ).flow
    }
}