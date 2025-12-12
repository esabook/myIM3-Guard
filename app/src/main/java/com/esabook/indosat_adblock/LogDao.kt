package com.esabook.indosat_adblock

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: LogEntity)

    @Update
    suspend fun update(user: LogEntity)

    @Delete
    suspend fun delete(user: LogEntity)

    @Query("SELECT * FROM log_table WHERE id = :id")
    fun getLogById(id: Int): Flow<LogEntity>

    @Query("SELECT * FROM log_table ORDER BY timestamp ASC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Query("SELECT * FROM log_table ORDER BY timestamp ASC")
    fun getAllLogsPagingSource(): PagingSource<Int, LogEntity>
}