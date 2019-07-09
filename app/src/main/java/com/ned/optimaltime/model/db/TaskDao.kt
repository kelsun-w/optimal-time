package com.ned.optimaltime.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ned.optimaltime.model.entity.Task

@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("DELETE FROM task_table")
    fun deleteAllTasks()

    @Query("SELECT * FROM task_table")
    fun getAllTasks() : LiveData<List<Task>>

}