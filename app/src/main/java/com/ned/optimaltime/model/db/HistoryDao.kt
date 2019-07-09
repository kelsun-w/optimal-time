package com.ned.optimaltime.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ned.optimaltime.model.entity.History

@Dao
interface HistoryDao {
    @Insert
    fun insert(history: History)

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)

    @Query("DELETE FROM history_table")
    fun deleteAllHistory()

    @Query("SELECT * FROM history_table")
    fun getAllHistory(): LiveData<List<History>>
}