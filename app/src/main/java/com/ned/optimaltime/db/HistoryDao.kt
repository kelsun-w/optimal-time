package com.ned.optimaltime.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ned.optimaltime.vo.DateCountFormat
import com.ned.optimaltime.vo.History

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

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','weekday 0', '-7 days','start of day')")
    fun getHistoryWeekly() : List<DateCountFormat>

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','start of month','start of day')")
    fun getHistoryMonthly() : List<DateCountFormat>

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','start of day')")
    fun getHistoryDaily() : List<DateCountFormat>

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','weekday 0', '-7 days','start of day'),task_id HAVING task_id == :uid")
    fun getHistoryWeeklyById(uid : Long) : List<DateCountFormat>

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','start of month','start of day'),task_id HAVING task_id == :uid")
    fun getHistoryMonthlyById(uid : Long) : List<DateCountFormat>

    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT date_recorded as date , count(*) as count from history_table GROUP BY strftime('%Y-%m-%d',date_recorded, 'unixepoch','start of day'),task_id HAVING task_id == :uid")
    fun getHistoryDailyById(uid : Long) : List<DateCountFormat>
}